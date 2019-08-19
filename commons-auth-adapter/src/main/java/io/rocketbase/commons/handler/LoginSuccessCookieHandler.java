package io.rocketbase.commons.handler;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.util.JwtTokenStore;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

public class LoginSuccessCookieHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String AUTH_REMEMBER = "authRemember";

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
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
