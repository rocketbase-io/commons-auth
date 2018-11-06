package io.rocketbase.commons.adapters;

import io.rocketbase.commons.resource.BaseRestResource;
import io.rocketbase.commons.util.JwtTokenDecoder;
import io.rocketbase.commons.util.JwtTokenDecoder.JwtTokenBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public class JwtClientRequestFactory extends HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory, BaseRestResource {

    protected String header = HttpHeaders.AUTHORIZATION;
    protected String tokenPrefix = "Bearer ";
    private JwtTokenProvider tokenProvider;
    private String lastToken;
    private LocalDateTime exp;

    public JwtClientRequestFactory(@NotNull JwtTokenProvider tokenProvider) {
        super(HttpClients.custom()
                .disableCookieManagement()
                .build()
        );
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void postProcessHttpRequest(HttpUriRequest request) {
        if (request.getFirstHeader(HttpHeaders.ACCEPT_LANGUAGE) == null) {
            request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, LocaleContextHolder.getLocale()
                    .getLanguage());
        }
        if (checkTokenProvidedNeedsRefresh()) {
            refreshToken();
        }
        if (request.getFirstHeader(header) == null && tokenProvider.getToken() != null) {
            request.addHeader(header, String.format("%s%s", tokenPrefix, tokenProvider.getToken()));
        }
    }

    protected boolean checkTokenProvidedNeedsRefresh() {
        if (tokenProvider.getToken() != null && tokenProvider.getRefreshToken() != null) {
            if (lastToken == null || !tokenProvider.getToken().equals(lastToken)) {
                JwtTokenBody tokenBody = JwtTokenDecoder.decodeTokenBody(tokenProvider.getToken());
                exp = tokenBody.getExpiration();
                if (exp == null) {
                    // in case of broken tokens
                    return true;
                }
                lastToken = tokenProvider.getToken();
            }

            return LocalDateTime.now(ZoneOffset.UTC)
                    .plusMinutes(1)
                    .isAfter(exp);
        }
        return false;
    }

    protected void refreshToken() {
        try {
            String uri = ensureEndsWithSlash(tokenProvider.getBaseAuthApiUrl()) + "auth/refresh";

            HttpUriRequest uriRequest = RequestBuilder.get()
                    .setUri(uri)
                    .setHeader(header, String.format("%s%s", tokenPrefix, tokenProvider.getRefreshToken()))
                    .build();
            HttpResponse response = getHttpClient().execute(uriRequest);
            String newToken = EntityUtils.toString(response.getEntity());
            tokenProvider.setToken(newToken);

            lastToken = null;
            exp = null;

            if (log.isTraceEnabled()) {
                log.trace("refreshed token before processing http-request");
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("couldn't refresh token. got error: {}", e.getMessage());
            }
        }
    }
}
