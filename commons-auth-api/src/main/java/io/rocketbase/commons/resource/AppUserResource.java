package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class AppUserResource implements BaseRestResource {

    public static final String API_USER = "/api/user/";
    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;

    public AppUserResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
    }

    public AppUserResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
    }

    @SneakyThrows
    public PageableResult<AppUserRead> find(int page, int pagesize) {
        ResponseEntity<PageableResult<AppUserRead>> response = restTemplate.exchange(appendParams(createUriComponentsBuilder(baseAuthApiUrl),
                PageRequest.of(page, pagesize))
                        .path(API_USER)
                        .toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());
        return response.getBody();
    }

    @SneakyThrows
    public PageableResult<AppUserRead> find(Pageable pageable) {
        ResponseEntity<PageableResult<AppUserRead>> response = restTemplate.exchange(appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                        .path(API_USER)
                        .toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());

        return response.getBody();
    }

    @SneakyThrows
    public AppUserRead create(AppUserCreate create) {
        ResponseEntity<AppUserRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_USER)
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(create, createHeaderWithLanguage()),
                AppUserRead.class);

        return response.getBody();
    }

    @SneakyThrows
    public AppUserRead patch(String id, AppUserUpdate update) {
        ResponseEntity<AppUserRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_USER)
                        .path(id)
                        .toUriString(),
                HttpMethod.PATCH,
                new HttpEntity<>(update, createHeaderWithLanguage()),
                AppUserRead.class);

        return response.getBody();
    }

    @SneakyThrows
    public void delete(String id) {
        restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_USER)
                        .path(id)
                        .toUriString(),
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaderWithLanguage()),
                AppUserRead.class);
    }

    protected ParameterizedTypeReference<PageableResult<AppUserRead>> createPagedTypeReference() {
        return new ParameterizedTypeReference<PageableResult<AppUserRead>>() {
        };
    }
}
