package io.rocketbase.commons.resource;

import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ForgotPasswordResource implements BaseRestResource {

    private String baseAuthApiUrl;
    private RestTemplate restTemplate;

    public ForgotPasswordResource(String baseAuthApiUrl) {
        this.baseAuthApiUrl = baseAuthApiUrl;
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
                .exchange(UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseAuthApiUrl))
                                .path("/auth/forgot-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(forgotPassword),
                        Void.class);
    }

    public void resetPassword(PerformPasswordResetRequest performPasswordReset) {
        ResponseEntity<Void> response = getRestTemplate()
                .exchange(UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseAuthApiUrl))
                                .path("/auth/reset-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(performPasswordReset),
                        Void.class);
    }
}
