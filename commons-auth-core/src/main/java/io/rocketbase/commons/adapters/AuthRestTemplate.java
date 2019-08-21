package io.rocketbase.commons.adapters;

import io.rocketbase.commons.resource.BasicResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class AuthRestTemplate extends RestTemplate implements RestOperations {

    /**
     * use SecurityContext as source for authentications
     */
    public AuthRestTemplate(AuthClientRequestFactory requestFactory) {
        super(requestFactory);
        init();
    }

    /**
     * login user once and work with it's credentials
     */
    public AuthRestTemplate(AuthClientLoginRequestFactory requestFactory) {
        super(requestFactory);
        init();
    }

    /**
     * login user once and work with it's credentials
     */
    public AuthRestTemplate(String baseAuthApiUrl, String username, String password) {
        super(new AuthClientLoginRequestFactory(baseAuthApiUrl, username, password));
        init();
    }

    protected void init() {
        setErrorHandler(new BasicResponseErrorHandler());
    }
}
