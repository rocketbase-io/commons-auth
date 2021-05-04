package io.rocketbase.commons.util;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.exception.TokenRefreshException;
import io.rocketbase.commons.resource.BaseRestResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;

@Slf4j
public class JwtTokenStoreHttp extends AbstractJwtTokenStore implements BaseRestResource {

    protected final String refreshUri;


    private HttpClient httpClient;

    /**
     * creates a refreshable TokenStore
     */
    public JwtTokenStoreHttp(String baseAuthApiUrl, JwtTokenBundle tokenBundle) {
        super(tokenBundle);
        Assert.hasText(baseAuthApiUrl, "baseAuthApiUrl is required");
        this.refreshUri = ensureEndsWithSlash(baseAuthApiUrl) + "auth/refresh";
    }

    /**
     * creates a not refreshable TokenStore
     */
    public JwtTokenStoreHttp(JwtTokenBundle tokenBundle) {
        super(tokenBundle);
        this.refreshUri = null;
    }

    protected HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClients.createSystem();
        }
        return httpClient;
    }

    /**
     * will perform a token refresh by using the
     */
    @Override
    public void refreshToken() throws TokenRefreshException {
        if (refreshUri == null)
            return;

        try {
            HttpUriRequest uriRequest = RequestBuilder.get()
                    .setUri(refreshUri)
                    .setHeader(header, getTokenHeader(tokenBundle.getRefreshToken()))
                    .build();
            HttpResponse response = getHttpClient().execute(uriRequest);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                if (log.isDebugEnabled()) {
                    log.debug("refresh token http-response with error. statusCode: {}", statusCode);
                }
            }
            String newToken = EntityUtils.toString(response.getEntity());
            // validate token
            JwtTokenBody tokenBody = JwtTokenDecoder.decodeTokenBody(newToken);
            if (tokenBody == null) {
                log.error("got invalid newToken during refreshToken");
                throw new TokenRefreshException();
            }

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
}
