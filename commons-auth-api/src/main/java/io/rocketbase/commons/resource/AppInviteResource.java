package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class AppInviteResource implements BaseRestResource {

    public static final String API_INVITE = "/api/invite/";
    protected RestTemplate restTemplate;
    protected String baseAuthApiUrl;

    public AppInviteResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
    }

    public AppInviteResource(JwtRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = restTemplate.getTokenProvider().getBaseAuthApiUrl();
    }

    public static void addQueryParams(QueryAppInvite query, UriComponentsBuilder uriBuilder) {
        if (query != null) {
            if (!StringUtils.isEmpty(query.getEmail())) {
                uriBuilder.queryParam("email", query.getEmail());
            }
            if (!StringUtils.isEmpty(query.getInvitor())) {
                uriBuilder.queryParam("invitor", query.getInvitor());
            }
            if (query.getExpired() != null) {
                uriBuilder.queryParam("expired", query.getExpired());
            }
        }
    }

    @SneakyThrows
    public PageableResult<AppInviteRead> find(QueryAppInvite query, Pageable pageable) {
        UriComponentsBuilder uriBuilder = appendParams(createUriComponentsBuilder(baseAuthApiUrl), pageable)
                .path(API_INVITE);
        addQueryParams(query, uriBuilder);

        ResponseEntity<PageableResult<AppInviteRead>> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                createPagedTypeReference());

        return response.getBody();
    }

    @SneakyThrows
    public AppInviteRead invite(InviteRequest inviteRequest) {
        ResponseEntity<AppInviteRead> response = restTemplate.exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path(API_INVITE)
                        .path("invite")
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(inviteRequest, createHeaderWithLanguage()),
                AppInviteRead.class);

        return response.getBody();
    }

    protected ParameterizedTypeReference<PageableResult<AppInviteRead>> createPagedTypeReference() {
        return new ParameterizedTypeReference<PageableResult<AppInviteRead>>() {
        };
    }
}
