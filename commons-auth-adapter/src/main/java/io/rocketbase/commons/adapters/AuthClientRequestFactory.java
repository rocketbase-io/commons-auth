package io.rocketbase.commons.adapters;


import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.util.JwtTokenStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthClientRequestFactory extends HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {

    @Override
    protected void postProcessHttpRequest(HttpUriRequest request) {
        JwtTokenStore jwtTokenStore = getCommonsAuthenticationToken().getJwtTokenStore();
        if (jwtTokenStore.checkTokenNeedsRefresh()) {
            jwtTokenStore.refreshToken();
        }
        request.setHeader(jwtTokenStore.getHeaderName(), jwtTokenStore.getTokenHeader());
    }

    protected CommonsAuthenticationToken getCommonsAuthenticationToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CommonsAuthenticationToken token;

        if (authentication == null) {
            throw new IllegalStateException("Cannot set authorization header because there is no authenticated principal");
        }

        if (!CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            throw new IllegalStateException(
                    String.format(
                            "Cannot set authorization header because Authentication is of type %s but %s is required",
                            authentication.getClass(), CommonsAuthenticationToken.class)
            );
        }

        token = (CommonsAuthenticationToken) authentication;
        return token;
    }

}
