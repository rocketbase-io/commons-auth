package io.rocketbase.commons.resource;

import io.rocketbase.commons.api.InviteApi;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * used for invited to lookup their details
 */
public class InviteResource implements BaseRestResource, InviteApi {

    public static final String AUTH_INVITE = "/auth/invite";

    protected String baseAuthApiUrl;
    protected RestTemplate restTemplate;

    public InviteResource(String baseAuthApiUrl) {
        this(baseAuthApiUrl, null);
    }

    public InviteResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.restTemplate = restTemplate;
    }

    protected RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new BasicResponseErrorHandler());
        }
        return restTemplate;
    }

    @Override
    public AppInviteRead verify(Long inviteId) {
        ResponseEntity<AppInviteRead> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path(AUTH_INVITE)
                                .queryParam("inviteId", inviteId).toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(createHeaderWithLanguage()),
                        AppInviteRead.class);
        return response.getBody();
    }

    @Override
    public AppUserRead transformToUser(ConfirmInviteRequest confirmInvite) {
        ResponseEntity<AppUserRead> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path(AUTH_INVITE).toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(confirmInvite, createHeaderWithLanguage()),
                        AppUserRead.class);
        return response.getBody();
    }

}
