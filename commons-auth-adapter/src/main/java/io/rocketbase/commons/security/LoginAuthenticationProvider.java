package io.rocketbase.commons.security;

import io.rocketbase.commons.api.LoginApi;
import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.service.JwtTokenStoreProvider;
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

import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private final LoginApi loginApi;
    private final JwtTokenService jwtTokenService;
    private final JwtTokenStoreProvider jwtTokenStoreProvider;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            LoginResponse login = loginApi.login(new LoginRequest(authentication.getName(), String.valueOf(authentication.getCredentials())));
            Collection<GrantedAuthority> authorities = jwtTokenService.getAuthoritiesFromToken(login.getJwtTokenBundle().getToken());
            return new CommonsAuthenticationToken(authorities, login.getUser(), jwtTokenStoreProvider.getInstance(login.getJwtTokenBundle()));
        } catch (HttpClientErrorException e) {
            if (Arrays.asList(HttpStatus.FORBIDDEN, HttpStatus.UNAUTHORIZED).contains(e.getStatusCode())) {
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
