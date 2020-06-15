package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.convert.QueryAppUserConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserReference;
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

public class UserSearchResource implements BaseRestResource {

    public static final String API_USER = "/api/user-search/";
    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;
    protected QueryAppUserConverter converter;

    public UserSearchResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.converter = new QueryAppUserConverter();
    }

    public UserSearchResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
        this.converter = new QueryAppUserConverter();
    }

    @SneakyThrows
    public PageableResult<AppUserReference> search(QueryAppUser query, Pageable pageable) {
        UriComponentsBuilder uriBuilder = appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                .path(API_USER);
        converter.addParams(uriBuilder, query);

        ResponseEntity<PageableResult<SimpleAppUserToken>> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());

        return (PageableResult) response.getBody();
    }

    public Optional<AppUserReference> findByUsernameOrId(String usernameOrId) {
        ResponseEntity<SimpleAppUserToken> response;
        try {
            response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                            .path(API_USER)
                            .path(usernameOrId)
                            .toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaderWithLanguage()),
                    SimpleAppUserToken.class);
        } catch (NotFoundException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }

    protected ParameterizedTypeReference<PageableResult<SimpleAppUserToken>> createPagedTypeReference() {
        return new ParameterizedTypeReference<PageableResult<SimpleAppUserToken>>() {
        };
    }
}
