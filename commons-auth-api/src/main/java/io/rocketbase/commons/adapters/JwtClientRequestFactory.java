package io.rocketbase.commons.adapters;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.resource.BaseRestResource;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.validation.constraints.NotNull;

@Slf4j
public class JwtClientRequestFactory extends HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory, BaseRestResource {

    protected JwtTokenStore jwtTokenStore;

    public JwtClientRequestFactory(@NotNull JwtTokenProvider tokenProvider) {
        super(HttpClients.custom()
                .disableCookieManagement()
                .build()
        );
        jwtTokenStore = new JwtTokenStore(tokenProvider.getBaseAuthApiUrl(), new JwtTokenBundle(tokenProvider.getToken(), tokenProvider.getRefreshToken()));
    }

    @Override
    protected void postProcessHttpRequest(HttpUriRequest request) {
        if (request.getFirstHeader(HttpHeaders.ACCEPT_LANGUAGE) == null) {
            request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, LocaleContextHolder.getLocale()
                    .getLanguage());
        }
        if (jwtTokenStore.checkTokenNeedsRefresh()) {
            jwtTokenStore.refreshToken();
        }
        if (request.getFirstHeader(jwtTokenStore.getHeaderName()) == null) {
            request.addHeader(jwtTokenStore.getHeaderName(), jwtTokenStore.getTokenHeader());
        }
    }
}
