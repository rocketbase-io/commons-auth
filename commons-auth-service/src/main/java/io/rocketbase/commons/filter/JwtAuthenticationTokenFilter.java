package io.rocketbase.commons.filter;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUserTokenDetails;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.security.CustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.JwtTokenStoreProvider;
import io.rocketbase.commons.service.user.AppUserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
public class JwtAuthenticationTokenFilter extends JwtTokenFilter {

    @Resource
    private AppUserTokenService appUserTokenService;

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private CustomAuthoritiesProvider customAuthoritiesProvider;

    @Resource
    private JwtTokenStoreProvider jwtTokenStoreProvider;

    protected Authentication tryToAuthenticate(String authToken, String username, HttpServletRequest request) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = appUserTokenService.loadUserByUsername(username);

            if (!(userDetails instanceof AppUserTokenDetails)) {
                return null;
            }
            AppUserTokenDetails user = (AppUserTokenDetails) userDetails;

            if (jwtTokenService.validateToken(authToken, user.getAppUser())) {
                Collection<GrantedAuthority> authorities = jwtTokenService.parseToken(authToken).getAuthoritiesFromToken();
                authorities.addAll(customAuthoritiesProvider.getExtraSecurityContextAuthorities(user.getAppUserToken(), request));

                CommonsAuthenticationToken authentication = new CommonsAuthenticationToken(authorities, user.getAppUserToken(),
                        jwtTokenStoreProvider.getInstance(new JwtTokenBundle(authToken, null)), null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                if (log.isTraceEnabled()) {
                    log.trace("authenticated user {} with {}, setting security context", username, authorities);
                }
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
                return authentication;
            }
        }
        return null;
    }
}