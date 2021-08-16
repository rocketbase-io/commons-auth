package io.rocketbase.commons.handler;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.service.token.AuthorizationCode;
import io.rocketbase.commons.service.token.AuthorizationCodeService;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
public class LoginSuccessCookieHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String AUTH_REMEMBER = "authRemember";
    public static final String OAUTH_REDIRECT = "oauthRedirect";

    @Nullable
    private final AuthorizationCodeService authorizationCodeService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (authentication instanceof CommonsAuthenticationToken) {
            JwtTokenStore jwtTokenStore = ((CommonsAuthenticationToken) authentication).getJwtTokenStore();
            if (jwtTokenStore != null) {
                JwtTokenBundle tokenBundle = jwtTokenStore.getTokenBundle();
                if (tokenBundle != null && tokenBundle.getRefreshTokenExpiryDate() != null) {
                    Cookie authRemember = new Cookie(AUTH_REMEMBER, tokenBundle.getRefreshToken());
                    authRemember.setMaxAge((int) (tokenBundle.getRefreshTokenExpiryDate().getEpochSecond() - Instant.now().getEpochSecond()));
                    authRemember.setHttpOnly(false);
                    authRemember.setPath("/");
                    response.addCookie(authRemember);
                }
            }
            if (authorizationCodeService != null) {
                Cookie oauthRedirect = WebUtils.getCookie(request, OAUTH_REDIRECT);
                if (oauthRedirect != null && StringUtils.hasText(oauthRedirect.getValue())) {
                    authorizationCodeService.findByCode(oauthRedirect.getValue())
                            .ifPresent(v -> {
                                authorizationCodeService.delete(v.getCode());
                                AuthorizationCode code = AuthorizationCode.copyConfig(v, 300);
                                if (code.getUserId() == null) {
                                    // eventually not existing for code
                                    code.setUserId(((CommonsAuthenticationToken) authentication).getId());
                                }
                                authorizationCodeService.save(code);
                                try {
                                    response.sendRedirect(code.buildRedirectUri());
                                    Cookie cookie = new Cookie(LoginSuccessCookieHandler.OAUTH_REDIRECT, "");
                                    cookie.setMaxAge(0);
                                    response.addCookie(cookie);
                                } catch (IOException e) {
                                }
                            });
                }
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
