package io.rocketbase.commons.adapters;

import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class AuthRestTemplate extends RestTemplate implements RestOperations {

    public AuthRestTemplate(AuthClientRequestFactory requestFactory) {
        super(requestFactory);
    }
}
