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
    private RestTemplate restTemplate;

    public RegistrationResource(String baseAuthApiUrl) {
        this.baseAuthApiUrl = baseAuthApiUrl;
    }

    protected RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new BasicResponseErrorHandler());
        }
        return restTemplate;
    }

    public AppUserRead register(RegistrationRequest registration) {
        ResponseEntity<AppUserRead> response = getRestTemplate()
                .exchange(UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseAuthApiUrl))
                                .path("/auth/register").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(registration),
                        AppUserRead.class);
        return response.getBody();
    }

    public JwtTokenBundle verify(String verification) {
        ResponseEntity<JwtTokenBundle> response = getRestTemplate()
                .exchange(UriComponentsBuilder.fromUriString(ensureEndsWithSlash(baseAuthApiUrl))
                                .path("/auth/verify")
                                .queryParam("verification", verification).toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(null),
                        JwtTokenBundle.class);
        return response.getBody();
    }
}
