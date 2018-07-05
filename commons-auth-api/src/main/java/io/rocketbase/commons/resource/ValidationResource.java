package io.rocketbase.commons.resource;

import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.dto.validation.ValidationResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ValidationResource implements BaseRestResource {

    private String baseAuthApiUrl;
    private RestTemplate restTemplate;

    public ValidationResource(String baseAuthApiUrl) {
        this.baseAuthApiUrl = baseAuthApiUrl;
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
                .exchange(UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseAuthApiUrl))
                                .path("/auth/validate/password").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(password),
                        new ParameterizedTypeReference<ValidationResponse<PasswordErrorCodes>>() {
                        });
        return response.getBody();
    }

    public ValidationResponse<UsernameErrorCodes> validateUsername(String username) {
        ResponseEntity<ValidationResponse<UsernameErrorCodes>> response = getRestTemplate()
                .exchange(UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseAuthApiUrl))
                                .path("/auth/validate/username").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(username),
                        new ParameterizedTypeReference<ValidationResponse<UsernameErrorCodes>>() {
                        });
        return response.getBody();
    }

    public ValidationResponse<EmailErrorCodes> validateEmail(String email) {
        ResponseEntity<ValidationResponse<EmailErrorCodes>> response = getRestTemplate()
                .exchange(UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseAuthApiUrl))
                                .path("/auth/validate/email").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(email),
                        new ParameterizedTypeReference<ValidationResponse<EmailErrorCodes>>() {
                        });
        return response.getBody();
    }
}
