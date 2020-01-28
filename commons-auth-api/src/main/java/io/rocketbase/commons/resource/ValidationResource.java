package io.rocketbase.commons.resource;

import io.rocketbase.commons.dto.validation.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class ValidationResource implements BaseRestResource {

    protected String baseAuthApiUrl;
    protected RestTemplate restTemplate;

    public ValidationResource(String baseAuthApiUrl) {
        this(baseAuthApiUrl, null);
    }

    public ValidationResource(String baseAuthApiUrl, RestTemplate restTemplate) {
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

    public ValidationResponse<PasswordErrorCodes> validatePassword(String password) {
        ResponseEntity<ValidationResponse<PasswordErrorCodes>> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/validate/password").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(password, createHeaderWithLanguage()),
                        new ParameterizedTypeReference<ValidationResponse<PasswordErrorCodes>>() {
                        });
        return response.getBody();
    }

    public ValidationResponse<UsernameErrorCodes> validateUsername(String username) {
        ResponseEntity<ValidationResponse<UsernameErrorCodes>> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/validate/username").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(username, createHeaderWithLanguage()),
                        new ParameterizedTypeReference<ValidationResponse<UsernameErrorCodes>>() {
                        });
        return response.getBody();
    }

    public ValidationResponse<EmailErrorCodes> validateEmail(String email) {
        ResponseEntity<ValidationResponse<EmailErrorCodes>> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/validate/email").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(email, createHeaderWithLanguage()),
                        new ParameterizedTypeReference<ValidationResponse<EmailErrorCodes>>() {
                        });
        return response.getBody();
    }

    public ValidationResponse<TokenErrorCodes> validateToken(String token) {
        ResponseEntity<ValidationResponse<TokenErrorCodes>> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/validate/token").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(token, createHeaderWithLanguage()),
                        new ParameterizedTypeReference<ValidationResponse<TokenErrorCodes>>() {
                        });
        return response.getBody();
    }
}
