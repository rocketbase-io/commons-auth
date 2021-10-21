package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.api.AuthenticationApi;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.EmailChangeRequest;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UsernameChangeRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.UsernameValidationException;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * endpoints to update authenticated user itself
 */
public class AuthenticationResource implements BaseRestResource, AuthenticationApi {

    protected String baseAuthApiUrl;
    protected RestTemplate restTemplate;


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
    @Override
    public AppUserToken getAuthenticated() {
        ResponseEntity<AppUserToken> response = restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/me").toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(createHeaderWithLanguage()),
                        AppUserToken.class);
        return response.getBody();
    }

    /**
     * perform a password change for a logged in user
     *
     * @param passwordChange change request
     */
    @Override
    public JwtTokenBundle changePassword(PasswordChangeRequest passwordChange) {
        ResponseEntity<JwtTokenBundle> response = restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/change-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(passwordChange, createHeaderWithLanguage()),
                        JwtTokenBundle.class);
        return response.getBody();
    }

    @Override
    public AppUserRead changeUsername(UsernameChangeRequest usernameChange) throws UsernameValidationException {
        ResponseEntity<AppUserRead> response = restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/change-username").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(usernameChange, createHeaderWithLanguage()),
                        AppUserRead.class);
        return response.getBody();
    }

    @Override
    public ExpirationInfo<AppUserRead> changeEmail(EmailChangeRequest emailChange) throws EmailValidationException {
        ResponseEntity<ExpirationInfo<AppUserRead>> response = restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/change-email").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(emailChange, createHeaderWithLanguage()),
                        new ParameterizedTypeReference<ExpirationInfo<AppUserRead>>() {
                        });
        return response.getBody();
    }

    @Override
    public AppUserRead verifyEmail(String verification) {
        ResponseEntity<AppUserRead> response = restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/verify-email")
                                .queryParam("verification", verification).toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(createHeaderWithLanguage()),
                        AppUserRead.class);
        return response.getBody();
    }

    /**
     * update user profile details for logged in user
     *
     * @param userProfile change request
     */
    @Override
    public AppUserRead updateProfile(UserProfile userProfile) {
        ResponseEntity<AppUserRead> response = restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/update-profile").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(userProfile, createHeaderWithLanguage()),
                        AppUserRead.class);
        return response.getBody();
    }

    /**
     * update user settings for logged in user
     *
     * @param userSetting change request
     */
    @Override
    public AppUserRead updateSetting(UserSetting userSetting) {
        ResponseEntity<AppUserRead> response = restTemplate
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/update-setting").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(userSetting, createHeaderWithLanguage()),
                        AppUserRead.class);
        return response.getBody();
    }

}
