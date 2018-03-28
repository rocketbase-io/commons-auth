package io.rocketbase.commons.resource;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class RegistrationResource implements BaseRestResource {

    private String baseAuthApiUrl;

    public RegistrationResource(String baseAuthApiUrl) {
        this.baseAuthApiUrl = baseAuthApiUrl;
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

    public AppUserRead register(RegistrationRequest registration) {
        ResponseEntity<AppUserRead> response = getDefaultRestTemplate()
                .exchange(UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseAuthApiUrl))
                                .path("/auth/register").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(registration),
                        AppUserRead.class);
        return handleResponse(response);
    }

    public JwtTokenBundle verify(String verification) {
        ResponseEntity<JwtTokenBundle> response = getDefaultRestTemplate()
                .exchange(UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseAuthApiUrl))
                                .path("/auth/verify")
                                .queryParam("verification", verification).toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(null),
                        JwtTokenBundle.class);
        return handleResponse(response);
    }
}
