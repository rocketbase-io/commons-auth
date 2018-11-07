package io.rocketbase.commons.util;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.exception.TokenRefreshException;
import io.rocketbase.commons.resource.BaseRestResource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public class JwtTokenStore implements Serializable, BaseRestResource {

    protected final String refreshUri;

    @Getter
    protected final JwtTokenBundle tokenBundle;

    protected String header = HttpHeaders.AUTHORIZATION;
    protected String tokenPrefix = "Bearer ";

    protected String lastToken;
    protected LocalDateTime exp;

    private HttpClient httpClient;

    public JwtTokenStore(String baseAuthApiUrl, JwtTokenBundle tokenBundle) {
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        Assert.notNull(tokenBundle, "tokenBundle is required");

        this.refreshUri = ensureEndsWithSlash(baseAuthApiUrl) + "auth/refresh";
        this.tokenBundle = tokenBundle;
    }

    protected HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClients.createSystem();
        }
        return httpClient;
    }

    /**
     * will check token's expiration date
     *
     * @return true if token is valid for at least 60 sec
     */
    public boolean checkTokenNeedsRefresh() {
        if (tokenBundle.getRefreshToken() != null) {
            if (lastToken == null || !tokenBundle.getToken().equals(lastToken)) {
                JwtTokenDecoder.JwtTokenBody tokenBody = JwtTokenDecoder.decodeTokenBody(tokenBundle.getToken());
                exp = tokenBody.getExpiration();
                if (exp == null) {
                    // in case of broken tokens
                    return true;
                }
                lastToken = tokenBundle.getToken();
            }

            return LocalDateTime.now(ZoneOffset.UTC)
                    .plusSeconds(60)
                    .isAfter(exp);
        }
        return false;
    }

    /**
     * will perform a token refresh by using the
     */
    public void refreshToken() throws TokenRefreshException {
        try {
            HttpUriRequest uriRequest = RequestBuilder.get()
                    .setUri(refreshUri)
                    .setHeader(header, getTokenHeader(tokenBundle.getRefreshToken()))
                    .build();
            HttpResponse response = getHttpClient().execute(uriRequest);
            String newToken = EntityUtils.toString(response.getEntity());
            tokenBundle.setToken(newToken);

            lastToken = null;
            exp = null;

            if (log.isTraceEnabled()) {
                log.trace("refreshed token before processing http-request");
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("couldn't refresh token. got error: {}", e.getMessage());
            }
            throw new TokenRefreshException();
        }
    }

    protected String getTokenHeader(String token) {
        return String.format("%s%s", tokenPrefix, token);
    }

    public String getHeaderName() {
        return header;
    }

    public String getTokenHeader() {
        return getTokenHeader(tokenBundle.getToken());
    }
}