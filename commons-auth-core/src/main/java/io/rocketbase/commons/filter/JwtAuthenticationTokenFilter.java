package io.rocketbase.commons.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private JwtTokenService jwtTokenService;

    @Resource
    private JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authToken = getAuthToken(request);
        String username = getValidatedUsername(authToken);

        setAuthenticationIfValid(authToken, username, request);

        chain.doFilter(request, response);
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

    protected void setAuthenticationIfValid(String authToken, String username, HttpServletRequest request) {
        if (username != null && SecurityContextHolder.getContext()
                .getAuthentication() == null) {
            AppUser user = (AppUser) appUserService.loadUserByUsername(username);

            if (jwtTokenService.validateToken(authToken, user)) {

                Collection<? extends GrantedAuthority> authorities = jwtTokenService.getAuthoritiesFromToken(authToken);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "", authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                if (log.isTraceEnabled()) {
                    log.trace("authenticated user {} with {}, setting security context", username, authorities);
                }
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        }
    }
}