package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.api.AppUserApi;
import io.rocketbase.commons.convert.QueryAppUserConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appuser.*;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.SimpleAppUserToken;
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
 * api resource used by authenticated users
 */
public class AppUserResource implements BaseRestResource, AppUserApi {

    public static final String API_USER = "/api/user/";
    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppUserConverter converter;

    public AppUserResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.converter = new QueryAppUserConverter();
    }

    public AppUserResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
        this.converter = new QueryAppUserConverter();
    }

    @Override
    @SneakyThrows
    public PageableResult<AppUserRead> find(QueryAppUser query, Pageable pageable) {
        UriComponentsBuilder uriBuilder = appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                .path(API_USER);
        converter.addParams(uriBuilder, query);

        ResponseEntity<PageableResult<AppUserRead>> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());

        return response.getBody();
    }

    @Override
    @SneakyThrows
    public Optional<AppUserRead> findOne(String usernameOrId) {
        ResponseEntity<AppUserRead> response;
        try {
            response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                            .path(API_USER)
                            .path(usernameOrId)
                            .toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaderWithLanguage()),
                    AppUserRead.class);
        } catch (NotFoundException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(response.getBody());
    }

    @Override
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

    @Override
    public AppUserRead resetPassword(String usernameOrId, AppUserResetPassword reset) {
        ResponseEntity<AppUserRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_USER).pathSegment(usernameOrId, "password")
                        .toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>(reset, createHeaderWithLanguage()),
                AppUserRead.class);

        return response.getBody();
    }

    @Override
    @SneakyThrows
    public AppUserRead patch(String usernameOrId, AppUserUpdate update) {
        ResponseEntity<AppUserRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_USER)
                        .path(usernameOrId)
                        .toUriString(),
                HttpMethod.PATCH,
                new HttpEntity<>(update, createHeaderWithLanguage()),
                AppUserRead.class);

        return response.getBody();
    }

    @Override
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

    @Override
    @SneakyThrows
    public AppInviteRead invite(InviteRequest inviteRequest) {
        ResponseEntity<AppInviteRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_USER)
                        .path("invite")
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(inviteRequest, createHeaderWithLanguage()),
                AppInviteRead.class);

        return response.getBody();
    }

    protected ParameterizedTypeReference<PageableResult<AppUserRead>> createPagedTypeReference() {
        return new ParameterizedTypeReference<PageableResult<AppUserRead>>() {
        };
    }
}
