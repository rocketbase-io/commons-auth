package io.rocketbase.commons.adapters;

import io.rocketbase.commons.resource.JwtTokenProvider;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class JwtRestTemplate extends RestTemplate implements RestOperations {

    public JwtRestTemplate(JwtTokenProvider tokenProvider) {
        super(new JwtClientRequestFactory(tokenProvider));
    }

    public JwtRestTemplate(ClientHttpRequestFactory factory) {
        super(factory);
    }

}
