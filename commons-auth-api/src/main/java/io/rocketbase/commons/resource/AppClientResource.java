package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.api.AppClientApi;
import io.rocketbase.commons.convert.QueryAppClientConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.dto.appclient.AppClientWrite;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
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

public class AppClientResource implements BaseRestResource, AppClientApi {

    public static final String API_CLIENT = "/api/client/";

    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppClientConverter converter;

    public AppClientResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.converter = new QueryAppClientConverter();
    }

    public AppClientResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
        this.converter = new QueryAppClientConverter();
    }

    @Override
    public PageableResult<AppClientRead> find(QueryAppClient query, Pageable pageable) {
        UriComponentsBuilder uriBuilder = appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                .path(API_CLIENT);
        converter.addParams(uriBuilder, query);

        ResponseEntity<PageableResult<AppClientRead>> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());

        return response.getBody();
    }

    @Override
    public Optional<AppClientRead> findById(Long id) {
        UriComponentsBuilder uriBuilder = createUriComponentsBuilder(baseAuthApiUrl)
                .path(API_CLIENT).pathSegment(String.valueOf(id));
        try {
            ResponseEntity<AppClientRead> response = restTemplate.exchange(uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaderWithLanguage()),
                    AppClientRead.class);
            return Optional.of(response.getBody());
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public AppClientRead create(AppClientWrite write) {
        ResponseEntity<AppClientRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_CLIENT)
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppClientRead.class);

        return response.getBody();
    }

    @Override
    public AppClientRead update(Long id, AppClientWrite write) {
        ResponseEntity<AppClientRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_CLIENT)
                        .pathSegment(String.valueOf(id))
                        .toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppClientRead.class);

        return response.getBody();    }

    @Override
    public void delete(Long id) {
        restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_CLIENT)
                        .pathSegment(String.valueOf(id))
                        .toUriString(),
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaderWithLanguage()),
                Void.class);
    }

    protected ParameterizedTypeReference<PageableResult<AppClientRead>> createPagedTypeReference() {
        return new ParameterizedTypeReference<PageableResult<AppClientRead>>() {
        };
    }
}
