package io.rocketbase.commons.filter;

import io.rocketbase.commons.api.LoginApi;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.handler.LoginSuccessCookieHandler;
import io.rocketbase.commons.model.TokenParseResult;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.security.CustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.JwtTokenStoreProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class LoginCookieFilter extends OncePerRequestFilter {

    private final LoginApi loginApi;
    private final JwtTokenService jwtTokenService;
    private final CustomAuthoritiesProvider customAuthoritiesProvider;
    private final JwtTokenStoreProvider jwtTokenStoreProvider;

    public static void removeAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(LoginSuccessCookieHandler.AUTH_REMEMBER, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Cookie authRemember = WebUtils.getCookie(request, LoginSuccessCookieHandler.AUTH_REMEMBER);
            if (authRemember != null && !StringUtils.isEmpty(authRemember.getValue())) {
                try {
                    String cookieRefreshToken = authRemember.getValue();
                    // check if token is valid
                    jwtTokenService.parseToken(cookieRefreshToken);
                    // get new accessToken
                    String accessToken = loginApi.getNewAccessToken(cookieRefreshToken);
                    // use accessToken in order to get also keyValues etc
                    TokenParseResult parsedToken = jwtTokenService.parseToken(accessToken);

                    Collection<GrantedAuthority> authorities = parsedToken.getAuthoritiesFromToken();
                    if (customAuthoritiesProvider != null) {
                        authorities.addAll(customAuthoritiesProvider.getExtraSecurityContextAuthorities(parsedToken.getUser(), request));
                    }

                    CommonsAuthenticationToken authentication = new CommonsAuthenticationToken(authorities, parsedToken.getUser(),
                            jwtTokenStoreProvider.getInstance(new JwtTokenBundle(accessToken, cookieRefreshToken)));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    if (log.isTraceEnabled()) {
                        log.trace("authenticated user {} with {}, setting security context", parsedToken.getUser().getUsername(), authorities);
                    }
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                } catch (Exception e) {
                    // delete invalid cookie
                    removeAuthCookie(response);
                    log.warn("tried to login via cookie. {}", e.getMessage());
                }
            }
        }

        chain.doFilter(request, response);
    }

}
