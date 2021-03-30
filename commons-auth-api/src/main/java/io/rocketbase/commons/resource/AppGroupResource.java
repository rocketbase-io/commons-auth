package io.rocketbase.commons.resource;

import io.rocketbase.commons.api.AppGroupApi;
import io.rocketbase.commons.convert.QueryAppGroupConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class AppGroupResource implements BaseRestResource, AppGroupApi {

    public static final String API_GROUP = "/api/group/";
    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppGroupConverter converter;

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
                        .path(API_GROUP).pathSegment(String.valueOf(id))
                        .toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppGroupRead.class);

        return response.getBody();
    }

    @Override
    public void delete(String id) {
        restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .pathSegment(API_GROUP, String.valueOf(id))
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
