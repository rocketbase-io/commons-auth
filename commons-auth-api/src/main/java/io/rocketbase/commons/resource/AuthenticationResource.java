package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AuthenticationResource {

    protected JwtRestTemplate jwtRestTemplate;
    protected RestTemplate restTemplate;
    protected String header = HttpHeaders.AUTHORIZATION;
    protected String tokenPrefix = "Bearer ";

    public AuthenticationResource(JwtRestTemplate jwtRestTemplate) {
        this.jwtRestTemplate = jwtRestTemplate;
    }

    protected RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new BasicResponseErrorHandler());
        }
        return restTemplate;
    }

    /**
     * login via username and password
     *
     * @param login credentials
     * @return token bundle with access- and refresh-token + user details
     */
    public LoginResponse login(LoginRequest login) {
        ResponseEntity<LoginResponse> response = getRestTemplate()
                .exchange(jwtRestTemplate.getBaseAuthApiBuilder()
                                .path("/auth/login").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(login),
                        LoginResponse.class);
        return response.getBody();
    }

    /**
     * get details of logged in user
     *
     * @return user details
     */
    public AppUserRead getAuthenticated() {
        ResponseEntity<AppUserRead> response = jwtRestTemplate
                .exchange(jwtRestTemplate.getBaseAuthApiBuilder()
                                .path("/auth/me").toUriString(),
                        HttpMethod.GET,
                        null,
                        AppUserRead.class);
        return response.getBody();
    }

    /**
     * perform a password change for a logged in user
     *
     * @param passwordChange change request
     */
    public void changePassword(PasswordChangeRequest passwordChange) {
        jwtRestTemplate
                .exchange(jwtRestTemplate.getBaseAuthApiBuilder()
                                .path("/auth/change-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(passwordChange),
                        Void.class);
    }

    /**
     * update user profile details for logged in user
     *
     * @param updateProfile change request
     */
    public void updateProfile(UpdateProfileRequest updateProfile) {
        jwtRestTemplate
                .exchange(jwtRestTemplate.getBaseAuthApiBuilder()
                                .path("/auth/update-profile").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(updateProfile),
                        Void.class);
    }

    /**
     * uses refreshToken from tokenProvider and updates token after success
     */
    public void refreshToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(header, String.format("%s%s", tokenPrefix, jwtRestTemplate.getTokenProvider().getRefreshToken()));

        ResponseEntity<String> response = getRestTemplate().exchange(jwtRestTemplate.getBaseAuthApiBuilder()
                        .path("/auth/refresh").toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        jwtRestTemplate.getTokenProvider().setToken(response.getBody());
    }

}
