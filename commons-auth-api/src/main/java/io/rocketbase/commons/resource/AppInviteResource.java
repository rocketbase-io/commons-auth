package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.api.AppInviteApi;
import io.rocketbase.commons.convert.QueryAppInviteConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.exception.NotFoundException;
import lombok.SneakyThrows;
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
 * backend/admin api to interact with invite entities
 */
public class AppInviteResource implements BaseRestResource, AppInviteApi {

    public static final String API_INVITE = "/api/invite/";

    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppInviteConverter converter;

    public AppInviteResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.converter = new QueryAppInviteConverter();
    }

    public AppInviteResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
        this.converter = new QueryAppInviteConverter();
    }

    @Override
    @SneakyThrows
    public PageableResult<AppInviteRead> find(QueryAppInvite query, Pageable pageable) {
        UriComponentsBuilder uriBuilder = appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                .path(API_INVITE);
        converter.addParams(uriBuilder, query);

        ResponseEntity<PageableResult<AppInviteRead>> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());

        return response.getBody();
    }

    @Override
    public Optional<AppInviteRead> findById(Long id) {
        UriComponentsBuilder uriBuilder = createUriComponentsBuilder(baseAuthApiUrl)
                .path(API_INVITE).pathSegment(String.valueOf(id));
        try {
            ResponseEntity<AppInviteRead> response = restTemplate.exchange(uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaderWithLanguage()),
                    AppInviteRead.class);
            return Optional.of(response.getBody());
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    @SneakyThrows
    public AppInviteRead invite(InviteRequest inviteRequest) {
        ResponseEntity<AppInviteRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_INVITE)
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(inviteRequest, createHeaderWithLanguage()),
                AppInviteRead.class);

        return response.getBody();
    }

    @Override
    @SneakyThrows
    public void delete(Long id) {
        restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .pathSegment(API_INVITE, String.valueOf(id))
                        .toUriString(),
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaderWithLanguage()),
                Void.class);
    }

    protected ParameterizedTypeReference<PageableResult<AppInviteRead>> createPagedTypeReference() {
        return new ParameterizedTypeReference<PageableResult<AppInviteRead>>() {
        };
    }
}
