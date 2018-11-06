package io.rocketbase.commons.resource;

import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class ForgotPasswordResource implements BaseRestResource {

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

    public void forgotPassword(ForgotPasswordRequest forgotPassword) {
        ResponseEntity<Void> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/forgot-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(forgotPassword),
                        Void.class);
    }

    public void resetPassword(PerformPasswordResetRequest performPasswordReset) {
        ResponseEntity<Void> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/reset-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(performPasswordReset),
                        Void.class);
    }
}
