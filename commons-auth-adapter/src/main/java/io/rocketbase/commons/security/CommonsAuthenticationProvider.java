package io.rocketbase.commons.security;

import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.resource.AuthenticationResource;
import io.rocketbase.commons.resource.LoginResource;
import io.rocketbase.commons.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;

@RequiredArgsConstructor
public class CommonsAuthenticationProvider implements AuthenticationProvider {

    private final LoginResource loginResource;

    private final JwtTokenService jwtTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            LoginResponse login = loginResource.login(new LoginRequest(authentication.getName(), String.valueOf(authentication.getCredentials())));
            Collection<GrantedAuthority> authorities = jwtTokenService.getAuthoritiesFromToken(login.getJwtTokenBundle().getToken());
            return new UsernamePasswordAuthenticationToken(login.getUser(), login.getJwtTokenBundle(),
                    authorities);
        } catch (HttpClientErrorException e) {
            throw new BadCredentialsException("wrong username/password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
