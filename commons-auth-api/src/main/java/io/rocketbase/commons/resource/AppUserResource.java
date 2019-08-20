package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
        return find(PageRequest.of(page, pagesize));
    }

    @SneakyThrows
    public PageableResult<AppUserRead> find(Pageable pageable) {
        return find(null, pageable);
    }

    @SneakyThrows
    public PageableResult<AppUserRead> find(QueryAppUser query, Pageable pageable) {
        UriComponentsBuilder uriBuilder = appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                .path(API_USER);
        if (query != null) {
            if (!StringUtils.isEmpty(query.getUsername())) {
                uriBuilder.queryParam("username", query.getUsername());
            }
            if (!StringUtils.isEmpty(query.getFirstName())) {
                uriBuilder.queryParam("firstName", query.getFirstName());
            }
            if (!StringUtils.isEmpty(query.getLastName())) {
                uriBuilder.queryParam("lastName", query.getLastName());
            }
            if (!StringUtils.isEmpty(query.getEmail())) {
                uriBuilder.queryParam("email", query.getEmail());
            }
            if (!StringUtils.isEmpty(query.getEnabled())) {
                uriBuilder.queryParam("enabled", query.getEnabled());
            }
            if (!StringUtils.isEmpty(query.getFreetext())) {
                uriBuilder.queryParam("freetext", query.getFreetext());
            }
        }

        ResponseEntity<PageableResult<AppUserRead>> response = restTemplate.exchange(uriBuilder.toUriString(),
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
