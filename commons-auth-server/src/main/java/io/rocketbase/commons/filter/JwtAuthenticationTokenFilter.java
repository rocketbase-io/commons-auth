package io.rocketbase.commons.filter;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.security.CustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
public class JwtAuthenticationTokenFilter extends JwtTokenFilter {

    @Resource
    private AppUserService appUserService;

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private CustomAuthoritiesProvider customAuthoritiesProvider;

    protected Authentication tryToAuthenticate(String authToken, String username, HttpServletRequest request) {
        if (username != null && SecurityContextHolder.getContext()
                .getAuthentication() == null) {
            AppUserEntity user = (AppUserEntity) appUserService.loadUserByUsername(username);

            if (jwtTokenService.validateToken(authToken, user)) {

                Collection<GrantedAuthority> authorities = jwtTokenService.getAuthoritiesFromToken(authToken);
                authorities.addAll(customAuthoritiesProvider.getExtraSecurityContextAuthorities(user, request));

                CommonsAuthenticationToken authentication = new CommonsAuthenticationToken(authorities, user,
                        new JwtTokenStore(new JwtTokenBundle(authToken, null)));
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