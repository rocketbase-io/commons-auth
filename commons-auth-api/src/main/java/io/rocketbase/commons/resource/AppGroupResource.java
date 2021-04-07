package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.api.AppGroupApi;
import io.rocketbase.commons.convert.QueryAppGroupConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.exception.NotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

public class AppGroupResource implements BaseRestResource, AppGroupApi {

    public static final String API_GROUP = "/api/group/";

    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppGroupConverter converter;

    public AppGroupResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.converter = new QueryAppGroupConverter();
    }

    public AppGroupResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
        this.converter = new QueryAppGroupConverter();
    }

    @Override
    public PageableResult<AppGroupRead> find(QueryAppGroup query, Pageable pageable) {
        UriComponentsBuilder uriBuilder = appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                .path(API_GROUP);
        converter.addParams(uriBuilder, query);

        ResponseEntity<PageableResult<AppGroupRead>> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());

        return response.getBody();
    }

    @Override
    public Optional<AppGroupRead> findById(Long id) {
        UriComponentsBuilder uriBuilder = createUriComponentsBuilder(baseAuthApiUrl)
                .path(API_GROUP).pathSegment(String.valueOf(id));
        try {
            ResponseEntity<AppGroupRead> response = restTemplate.exchange(uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaderWithLanguage()),
                    AppGroupRead.class);
            return Optional.of(response.getBody());
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public AppGroupRead create(Long parentId, AppGroupWrite write) {
        ResponseEntity<AppGroupRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_GROUP).pathSegment(String.valueOf(parentId))
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppGroupRead.class);

        return response.getBody();
    }

    @Override
    public AppGroupRead update(Long id, AppGroupWrite write) {
        ResponseEntity<AppGroupRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_GROUP)
                        .pathSegment(String.valueOf(id))
                        .toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppGroupRead.class);

        return response.getBody();
    }

    @Override
    public void delete(Long id) {
        restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_GROUP)
                        .pathSegment(String.valueOf(id))
                        .toUriString(),
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaderWithLanguage()),
                Void.class);
    }

    protected ParameterizedTypeReference<PageableResult<AppGroupRead>> createPagedTypeReference() {
        return new ParameterizedTypeReference<PageableResult<AppGroupRead>>() {
        };
    }
}
