package io.rocketbase.commons.resource;

import io.rocketbase.commons.api.LoginApi;
import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class LoginResource implements BaseRestResource, LoginApi {

    @Getter
    protected String baseAuthApiUrl;
    protected RestTemplate restTemplate;
    protected String header = HttpHeaders.AUTHORIZATION;
    protected String tokenPrefix = "Bearer ";

    public LoginResource(String baseAuthApiUrl) {
        this(baseAuthApiUrl, null);
    }

    public LoginResource(String baseAuthApiUrl, RestTemplate restTemplate) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.restTemplate = restTemplate;
        this.baseAuthApiUrl = baseAuthApiUrl;
    }

    protected RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new BasicResponseErrorHandler());
        }
        return restTemplate;
    }

    /**
     * login via username and password
     *
     * @param login credentials
     * @return token bundle with access- and refresh-token + user details
     */
    @Override
    public LoginResponse login(LoginRequest login) {
        ResponseEntity<LoginResponse> response = getRestTemplate()
                .exchange(createUriComponentsBuilder(baseAuthApiUrl)
                                .path("/auth/login").toUriString(),
                        HttpMethod.POST,
                        new HttpEntity<>(login, createHeaderWithLanguage()),
                        LoginResponse.class);
        return response.getBody();
    }


    /**
     * uses refreshToken and returns fresh accessToken
     *
     * @param refreshToken token that has been provided after login
     * @return new accessToken
     */
    @Override
    public String getNewAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(header, String.format("%s%s", tokenPrefix, refreshToken));
        headers.add("Accept-Language", LocaleContextHolder.getLocale().getLanguage());

        ResponseEntity<String> response = getRestTemplate().exchange(createUriComponentsBuilder(baseAuthApiUrl)
                        .path("/auth/refresh").toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        return response.getBody();
    }
}
