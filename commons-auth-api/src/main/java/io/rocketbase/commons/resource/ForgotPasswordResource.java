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
    public ExpirationInfo<AppUserRead> forgotPassword(ForgotPasswordRequest forgotPassword) {
        ResponseEntity<ExpirationInfo<AppUserRead>> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/forgot-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(forgotPassword, createHeaderWithLanguage()),
                        new ParameterizedTypeReference<ExpirationInfo<AppUserRead>>() {});
        return response.getBody();
    }

    @Override
    public void resetPassword(PerformPasswordResetRequest performPasswordReset) {
        ResponseEntity<Void> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/reset-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(performPasswordReset, createHeaderWithLanguage()),
                        Void.class);
    }
}
