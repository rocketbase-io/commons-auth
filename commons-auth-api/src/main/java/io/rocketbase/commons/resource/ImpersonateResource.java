package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * api resource used by admins to impersonate as someone else
 */
public class ImpersonateResource implements BaseRestResource {

    public static final String API_IMPERSONATE = "/api/impersonate/";
    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;

    public ImpersonateResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
    }

    public ImpersonateResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
    }

    @SneakyThrows
    public JwtTokenBundle impersonate(String userIdOrUsername) {
        return restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_IMPERSONATE)
                        .path(userIdOrUsername).toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                JwtTokenBundle.class).getBody();
    }

}
