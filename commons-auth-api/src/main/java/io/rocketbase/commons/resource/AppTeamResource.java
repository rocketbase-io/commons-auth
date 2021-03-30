package io.rocketbase.commons.resource;

import io.rocketbase.commons.api.AppTeamApi;
import io.rocketbase.commons.convert.QueryAppTeamConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.dto.appteam.AppTeamWrite;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class AppTeamResource implements BaseRestResource, AppTeamApi {

    public static final String API_TEAM = "/api/team/";
    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppTeamConverter converter;

    @Override
    public PageableResult<AppTeamRead> find(QueryAppTeam query, Pageable pageable) {
        UriComponentsBuilder uriBuilder = appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                .path(API_TEAM);
        converter.addParams(uriBuilder, query);

        ResponseEntity<PageableResult<AppTeamRead>> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());

        return response.getBody();
    }

    @Override
    public AppTeamRead create(AppTeamWrite write) {
        ResponseEntity<AppTeamRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_TEAM)
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppTeamRead.class);

        return response.getBody();
    }

    @Override
    public AppTeamRead update(Long id, AppTeamWrite write) {
        ResponseEntity<AppTeamRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_TEAM).pathSegment(String.valueOf(id))
                        .toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppTeamRead.class);

        return response.getBody();    }

    @Override
    public void delete(String id) {
        restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .pathSegment(API_TEAM, String.valueOf(id))
                        .toUriString(),
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaderWithLanguage()),
                Void.class);
    }

    protected ParameterizedTypeReference<PageableResult<AppTeamRead>> createPagedTypeReference() {
        return new ParameterizedTypeReference<PageableResult<AppTeamRead>>() {
        };
    }
}
