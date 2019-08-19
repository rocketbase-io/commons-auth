package io.rocketbase.commons.security;

import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.resource.LoginResource;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.Collection;

@RequiredArgsConstructor
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private final LoginResource loginResource;
    private final JwtTokenService jwtTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            LoginResponse login = loginResource.login(new LoginRequest(authentication.getName(), String.valueOf(authentication.getCredentials())));
            Collection<GrantedAuthority> authorities = jwtTokenService.getAuthoritiesFromToken(login.getJwtTokenBundle().getToken());
            return new CommonsAuthenticationToken(authorities, login.getUser(), new JwtTokenStore(loginResource.getBaseAuthApiUrl(), login.getJwtTokenBundle()));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                throw new BadCredentialsException("wrong username/password", e);
            } else {
                throw new InternalAuthenticationServiceException("got http error with status: " + e.getRawStatusCode(), e);
            }
        } catch (RestClientException e) {
            throw new InternalAuthenticationServiceException("service is not available?", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
