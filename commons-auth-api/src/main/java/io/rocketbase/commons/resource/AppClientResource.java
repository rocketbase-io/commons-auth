package io.rocketbase.commons.resource;

import io.rocketbase.commons.api.AppClientApi;
import io.rocketbase.commons.convert.QueryAppClientConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.dto.appclient.AppClientWrite;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class AppClientResource implements BaseRestResource, AppClientApi {

    public static final String API_CLIENT = "/api/client/";
    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppClientConverter converter;

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
                        .path(API_CLIENT).pathSegment(String.valueOf(id))
                        .toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppClientRead.class);

        return response.getBody();    }

    @Override
    public void delete(String id) {
        restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .pathSegment(API_CLIENT, String.valueOf(id))
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
