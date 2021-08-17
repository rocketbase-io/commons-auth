package io.rocketbase.commons.resource;

import io.rocketbase.commons.api.ForgotPasswordApi;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * public interactions for password forget flow
 */
public class ForgotPasswordResource implements BaseRestResource, ForgotPasswordApi {

    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;

    public ForgotPasswordResource(String baseAuthApiUrl) {
        this(baseAuthApiUrl, null);
    }

    public ForgotPasswordResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.restTemplate = restTemplate;
    }

    protected RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new BasicResponseErrorHandler());
        }
        return restTemplate;
    }

    @Override
    public ExpirationInfo<Void> forgotPassword(ForgotPasswordRequest forgotPassword) {
        ResponseEntity<ExpirationInfo<Void>> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/forgot-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(forgotPassword, createHeaderWithLanguage()),
                        new ParameterizedTypeReference<ExpirationInfo<Void>>() {});
        return response.getBody();
    }

    @Override
    public AppUserRead resetPassword(PerformPasswordResetRequest performPasswordReset) {
        ResponseEntity<AppUserRead> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/reset-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(performPasswordReset, createHeaderWithLanguage()),
                        AppUserRead.class);
        return response.getBody();
    }
}
