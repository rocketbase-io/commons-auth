package io.rocketbase.commons.test.adapters;

import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.resource.BasicResponseErrorHandler;
import io.rocketbase.commons.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class AuthRestTestTemplate extends RestTemplate {

    public AuthRestTestTemplate(AppUserToken userToken, JwtTokenService jwtTokenService) {
        super(new TestClientHttpRequestFactory(userToken, jwtTokenService));
        setErrorHandler(new BasicResponseErrorHandler());
    }

    @RequiredArgsConstructor
    protected static class TestClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {
        private final AppUserToken userToken;
        private final JwtTokenService jwtTokenService;

        @Override
        protected void postProcessHttpRequest(HttpUriRequest request) {
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenService.generateAccessToken(userToken));
        }
    }


}
