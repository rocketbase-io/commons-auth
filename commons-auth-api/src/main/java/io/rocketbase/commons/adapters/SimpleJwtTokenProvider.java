package io.rocketbase.commons.adapters;

import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.resource.JwtTokenProvider;
import lombok.Data;

@Data
public class SimpleJwtTokenProvider implements JwtTokenProvider {

    private String token;
    private String refreshToken;
    private String refreshTokenEndpoint;

    public SimpleJwtTokenProvider(String refreshTokenEndpoint) {
        this.refreshTokenEndpoint = refreshTokenEndpoint;
    }

    public SimpleJwtTokenProvider(String refreshTokenEndpoint, JwtTokenBundle jwtTokenBundle) {
        this.refreshTokenEndpoint = refreshTokenEndpoint;
        this.token = jwtTokenBundle.getToken();
        this.refreshToken = jwtTokenBundle.getRefreshToken();
    }
}
