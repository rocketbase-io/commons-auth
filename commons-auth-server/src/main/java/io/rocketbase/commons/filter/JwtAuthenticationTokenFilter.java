package io.rocketbase.commons.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.security.CustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private CustomAuthoritiesProvider customAuthoritiesProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authToken = getAuthToken(request);
        String username = getValidatedUsername(authToken);

        try {
            tryToAuthenticate(authToken, username, request);
            chain.doFilter(request, response);
        } catch (Exception e) {
            int status = HttpStatus.BAD_REQUEST.value();
            response.setStatus(status);
            response.getWriter().write(String.format("{\"status\": %d, \"message\": \"%s\"}", status,
                    e.getMessage().replace("\"", "\\")));
        }
    }

    protected String getAuthToken(HttpServletRequest request) {
        String authToken = null;

        final String requestHeader = request.getHeader(jwtProperties.getHeader());
        if (requestHeader != null && requestHeader.startsWith(jwtProperties.getTokenPrefix())) {
            // check header
            authToken = requestHeader.substring(jwtProperties.getTokenPrefix().length());
        } else if (request.getParameter(jwtProperties.getUriParam()) != null) {
            // check uiParam
            authToken = request.getParameter(jwtProperties.getUriParam());
        }
        return authToken;
    }

    protected String getValidatedUsername(String authToken) {
        if (authToken != null) {
            try {
                return jwtTokenService.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                log.error("an error occured during getting username from token. {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                log.warn("the token is expired and not valid anymore");
            } catch (MalformedJwtException e) {
                log.warn("the token has invalid format. {}", e.getMessage());
            } catch (JwtException e) {
                log.error("other token exception: {}", e.getMessage());
            }
        }
        return null;
    }

    protected Authentication tryToAuthenticate(String authToken, String username, HttpServletRequest request) {
        if (username != null && SecurityContextHolder.getContext()
                .getAuthentication() == null) {
            AppUser user = (AppUser) appUserService.loadUserByUsername(username);

            if (jwtTokenService.validateToken(authToken, user)) {

                Collection<GrantedAuthority> authorities = jwtTokenService.getAuthoritiesFromToken(authToken);
                authorities.addAll(customAuthoritiesProvider.getExtraSecurityContextAuthorities(user, request));

                // UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "", authorities);
                CommonsAuthenticationToken authentication = new CommonsAuthenticationToken(authorities, appUserConverter.fromEntity(user),
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