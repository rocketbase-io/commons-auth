package io.rocketbase.commons.adapters;


import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.exception.TokenRefreshException;
import io.rocketbase.commons.resource.BaseRestResource;
import io.rocketbase.commons.resource.LoginResource;
import io.rocketbase.commons.util.JwtTokenStore;
import io.rocketbase.commons.util.JwtTokenStoreHttp;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * factory that initially login user and work with it's tokenBundle<br>
 * main idea to use it for service or admin-tasks with your application
 */
@Slf4j
public class AuthClientLoginRequestFactory extends HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory, BaseRestResource {

    final String baseAuthApiUrl;
    final String username;
    final String password;

    JwtTokenStore jwtTokenStore;

    public AuthClientLoginRequestFactory(HttpClient httpClient, String baseAuthApiUrl, String username, String password) {
        super(httpClient);
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.username = username;
        this.password = password;
    }

    public AuthClientLoginRequestFactory(String baseAuthApiUrl, String username, String password) {
        super();
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    protected void postProcessHttpRequest(HttpUriRequest request) {
        JwtTokenStore jwtTokenStore = getJwtTokenStore();
        if (jwtTokenStore.checkTokenNeedsRefresh()) {
            try {
                jwtTokenStore.refreshToken();
            } catch (TokenRefreshException e) {
                jwtTokenStore = refreshJwtTokenStore();
            }
        }
        request.setHeader(jwtTokenStore.getHeaderName(), jwtTokenStore.getTokenHeader());
    }

    protected JwtTokenStore getJwtTokenStore() {
        if (jwtTokenStore == null) {
            refreshJwtTokenStore();
        }
        return jwtTokenStore;
    }

    protected JwtTokenStore initStore(LoginResponse loginResponse) {
        return new JwtTokenStoreHttp(baseAuthApiUrl, loginResponse.getJwtTokenBundle());
    }

    protected JwtTokenStore refreshJwtTokenStore() {
        LoginResponse loginResponse = new LoginResource(baseAuthApiUrl).login(new LoginRequest(username, password));
        jwtTokenStore = initStore(loginResponse);
        if (log.isDebugEnabled()) {
            log.debug("logged in with user: {}", username);
        }
        return jwtTokenStore;
    }


}