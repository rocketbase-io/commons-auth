package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.api.AppCapabilityApi;
import io.rocketbase.commons.convert.QueryAppCapabilityConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
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

public class AppCapabilityResource implements BaseRestResource, AppCapabilityApi {

    public static final String API_CAPABILITY = "/api/capability/";

    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppCapabilityConverter converter;

    public AppCapabilityResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.converter = new QueryAppCapabilityConverter();
    }

    public AppCapabilityResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
        this.converter = new QueryAppCapabilityConverter();
    }

    @Override
    public PageableResult<AppCapabilityRead> find(QueryAppCapability query, Pageable pageable) {
        UriComponentsBuilder uriBuilder = appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                .path(API_CAPABILITY);
        converter.addParams(uriBuilder, query);

        ResponseEntity<PageableResult<AppCapabilityRead>> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());

        return response.getBody();
    }

    @Override
    public Optional<AppCapabilityRead> findById(Long id) {
        UriComponentsBuilder uriBuilder = createUriComponentsBuilder(baseAuthApiUrl)
                .path(API_CAPABILITY).pathSegment(String.valueOf(id));
        try {
            ResponseEntity<AppCapabilityRead> response = restTemplate.exchange(uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaderWithLanguage()),
                    AppCapabilityRead.class);
            return Optional.of(response.getBody());
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public AppCapabilityRead create(Long parentId, AppCapabilityWrite write) {
        ResponseEntity<AppCapabilityRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_CAPABILITY).pathSegment(String.valueOf(parentId))
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppCapabilityRead.class);

        return response.getBody();
    }

    @Override
    public AppCapabilityRead update(Long id, AppCapabilityWrite write) {
        ResponseEntity<AppCapabilityRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_CAPABILITY).pathSegment(String.valueOf(id))
                        .toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppCapabilityRead.class);

        return response.getBody();    }

    @Override
    public void delete(Long id) {
        restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .pathSegment(API_CAPABILITY, String.valueOf(id))
                        .toUriString(),
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaderWithLanguage()),
                Void.class);
    }

    protected ParameterizedTypeReference<PageableResult<AppCapabilityRead>> createPagedTypeReference() {
        return new ParameterizedTypeReference<PageableResult<AppCapabilityRead>>() {
        };
    }
}
