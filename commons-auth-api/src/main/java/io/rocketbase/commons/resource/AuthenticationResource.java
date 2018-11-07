package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class AuthenticationResource implements BaseRestResource {

    protected String baseAuthApiUrl;
    protected RestTemplate restTemplate;
    protected String header = HttpHeaders.AUTHORIZATION;
    protected String tokenPrefix = "Bearer ";


    public AuthenticationResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
    }

    public AuthenticationResource(JwtRestTemplate jwtRestTemplate) {
        this.baseAuthApiUrl = jwtRestTemplate.getTokenProvider().getBaseAuthApiUrl();
        this.restTemplate = jwtRestTemplate;
    }

    /**
     * get details of logged in user
     *
     * @return user details
     */
    public AppUserRead getAuthenticated() {
        ResponseEntity<AppUserRead> response = restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
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
        restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
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
        restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/update-profile").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(updateProfile),
                        Void.class);
    }

}
