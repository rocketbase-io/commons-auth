package io.rocketbase.commons.resource;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class RegistrationResource implements BaseRestResource {

    protected String baseAuthApiUrl;
    protected RestTemplate restTemplate;

    public RegistrationResource(String baseAuthApiUrl) {
        this(baseAuthApiUrl, null);
    }

    public RegistrationResource(String baseAuthApiUrl, RestTemplate restTemplate) {
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

    public AppUserRead register(RegistrationRequest registration) {
        ResponseEntity<AppUserRead> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/register").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(registration),
                        AppUserRead.class);
        return response.getBody();
    }

    public JwtTokenBundle verify(String verification) {
        ResponseEntity<JwtTokenBundle> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/verify")
                                .queryParam("verification", verification).toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(null),
                        JwtTokenBundle.class);
        return response.getBody();
    }
}
