package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.AppUserRead;
import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.dto.LoginRequest;
import io.rocketbase.commons.dto.PasswordChangeRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AuthenticationResource {

    protected JwtRestTemplate restTemplate;
    protected String header = HttpHeaders.AUTHORIZATION;
    protected String tokenPrefix = "Bearer ";

    public AuthenticationResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected RestTemplate getDefaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new BasicResponseErrorHandler());
        return restTemplate;
    }

    protected <R> R handleResponse(ResponseEntity<R> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return null;
    }

    /**
     * login via username and password
     *
     * @param login credentials
     * @return token bundle with access- and refresh-token
     */
    public JwtTokenBundle login(LoginRequest login) {
        ResponseEntity<JwtTokenBundle> response = getDefaultRestTemplate()
                .exchange(restTemplate.getBaseAuthApiBuilder()
                                .path("/auth/login").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(login),
                        JwtTokenBundle.class);
        return handleResponse(response);
    }

    /**
     * get details of logged in user
     *
     * @return user details
     */
    public AppUserRead getAuthenticated() {
        ResponseEntity<AppUserRead> response = restTemplate
                .exchange(restTemplate.getBaseAuthApiBuilder()
                                .path("/auth/me").toUriString(),
                        HttpMethod.GET,
                        null,
                        AppUserRead.class);
        return handleResponse(response);
    }

    /**
     * perform a password change for a logged in user
     *
     * @param passwordChange change request
     */
    public void changePassword(PasswordChangeRequest passwordChange) {
        restTemplate
                .exchange(restTemplate.getBaseAuthApiBuilder()
                                .path("/auth/change-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(passwordChange),
                        Void.class);
    }

    /**
     * uses refreshToken from tokenProvider and updates token after success
     */
    public void refreshToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(header, String.format("%s%s", tokenPrefix, restTemplate.getTokenProvider().getRefreshToken()));

        ResponseEntity<String> response = getDefaultRestTemplate().exchange(restTemplate.getBaseAuthApiBuilder()
                        .path("/auth/refresh").toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        restTemplate.getTokenProvider().setToken(response.getBody());
    }

}
