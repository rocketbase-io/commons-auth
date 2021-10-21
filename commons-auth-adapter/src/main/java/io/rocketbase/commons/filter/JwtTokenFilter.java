package io.rocketbase.commons.filter;

import io.jsonwebtoken.JwtException;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.TokenParseResult;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.security.CustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.JwtTokenStoreProvider;
import io.rocketbase.commons.util.Nulls;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private CustomAuthoritiesProvider customAuthoritiesProvider;

    @Resource
    private JwtTokenStoreProvider jwtTokenStoreProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            TokenParseResult parsedToken = null;
            String authToken = getAuthToken(request);
            if (StringUtils.hasText(authToken)) {
                try {
                    parsedToken = jwtTokenService.parseToken(authToken);
                } catch (JwtException jwtException) {
                    log.debug("invalid token: {}", jwtException.getMessage());
                }
            }
            tryToAuthenticate(parsedToken, request);
            chain.doFilter(request, response);
        } catch (Exception e) {
            int status = HttpStatus.BAD_REQUEST.value();
            response.setStatus(status);
            response.getWriter().write(String.format("{\"status\": %d, \"message\": \"%s\"}", status,
                    Nulls.notNull(e.getMessage()).replace("\"", "\\")));
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

    protected Authentication tryToAuthenticate(TokenParseResult parsedToken, HttpServletRequest request) {
        if (parsedToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Collection<GrantedAuthority> authorities = parsedToken.getAuthoritiesFromToken();
            authorities.addAll(customAuthoritiesProvider.getExtraSecurityContextAuthorities(parsedToken.getUser(), request));

            CommonsAuthenticationToken authentication = new CommonsAuthenticationToken(authorities, parsedToken.getUser(),
                    jwtTokenStoreProvider.getInstance(new JwtTokenBundle(parsedToken.getToken(), null)), parsedToken.getClientId());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            if (log.isTraceEnabled()) {
                log.trace("authenticated user {} with {}, setting security context", parsedToken.getUser().getUsername(), authorities);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        }
        return null;
    }
}