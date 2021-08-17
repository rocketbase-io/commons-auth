package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.api.AppTeamApi;
import io.rocketbase.commons.convert.QueryAppTeamConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.dto.appteam.AppTeamWrite;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
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

/**
 * backend/admin api to interact with team entities
 */
public class AppTeamResource implements BaseRestResource, AppTeamApi {

    public static final String API_TEAM = "/api/team/";

    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppTeamConverter converter;

    public AppTeamResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.converter = new QueryAppTeamConverter();
    }

    public AppTeamResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
        this.converter = new QueryAppTeamConverter();
    }

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
    public Optional<AppTeamRead> findById(Long id) {
        UriComponentsBuilder uriBuilder = createUriComponentsBuilder(baseAuthApiUrl)
                .path(API_TEAM).pathSegment(String.valueOf(id));
        try {
            ResponseEntity<AppTeamRead> response = restTemplate.exchange(uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaderWithLanguage()),
                    AppTeamRead.class);
            return Optional.of(response.getBody());
        } catch (NotFoundException e) {
            return Optional.empty();
        }
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
                        .path(API_TEAM)
                        .pathSegment(String.valueOf(id))
                        .toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>(write, createHeaderWithLanguage()),
                AppTeamRead.class);

        return response.getBody();    }

    @Override
    public void delete(Long id) {
        restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_TEAM)
                        .pathSegment(String.valueOf(id))
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
