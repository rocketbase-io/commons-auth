package io.rocketbase.commons.resource;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.AppUserRead;
import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.dto.LoginRequest;
import io.rocketbase.commons.dto.PasswordChangeRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class AuthenticationResource {

    private JwtRestTemplate restTemplate;
    private String baseApiUrl;

    public AuthenticationResource(JwtRestTemplate restTemplate, String baseApiUrl) {
        this.restTemplate = restTemplate;
        this.baseApiUrl = baseApiUrl;
    }

    public UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromUriString(baseApiUrl + (baseApiUrl.endsWith("/") ? "" : "/"));
    }

    public JwtTokenBundle login(LoginRequest login) {
        ResponseEntity<JwtTokenBundle> response = restTemplate
                .exchange(getUriBuilder().path("/auth/login").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(login),
                        JwtTokenBundle.class);

        return response.getBody();
    }

    public AppUserRead getAuthenticated() {
        ResponseEntity<AppUserRead> response = restTemplate
                .exchange(getUriBuilder().path("/auth/me").toUriString(),
                        HttpMethod.GET,
                        null,
                        AppUserRead.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return response.getBody();
    }

    public void changePassword(PasswordChangeRequest passwordChange) {
        restTemplate
                .exchange(getUriBuilder().path("/auth/change-password").toUriString(),
                        HttpMethod.PUT,
                        new HttpEntity<>(passwordChange),
                        Void.class);
    }

}
