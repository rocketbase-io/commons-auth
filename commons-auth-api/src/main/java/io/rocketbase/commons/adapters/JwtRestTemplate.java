package io.rocketbase.commons.adapters;

import io.rocketbase.commons.resource.BasicResponseErrorHandler;
import lombok.Getter;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class JwtRestTemplate extends RestTemplate implements RestOperations {

    @Getter
    private JwtTokenProvider tokenProvider;

    /**
     * default constructor<br>
     * set's {@link BasicResponseErrorHandler} as errorHandler
     *
     * @param tokenProvider will get used to init an {@link JwtClientRequestFactory}
     */
    public JwtRestTemplate(JwtTokenProvider tokenProvider) {
        super(new JwtClientRequestFactory(tokenProvider));
        this.tokenProvider = tokenProvider;
        setErrorHandler(new BasicResponseErrorHandler());
    }

    /**
     * us in case you would like provide your own factory
     *
     * @param factory custom factory
     */
    public JwtRestTemplate(ClientHttpRequestFactory factory) {
        super(factory);
    }

    public UriComponentsBuilder getBaseAuthApiBuilder() {
        if (tokenProvider != null) {
            String baseAuthApiUrl = tokenProvider.getBaseAuthApiUrl();
            return UriComponentsBuilder.fromUriString(baseAuthApiUrl + (baseAuthApiUrl.endsWith("/") ? "" : "/"));
        }
        return UriComponentsBuilder.newInstance();
    }

}
