package io.rocketbase.commons.adapters;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import lombok.Data;

@Data
public class SimpleJwtTokenProvider implements JwtTokenProvider {

    private String token;
    private String refreshToken;
    private String baseAuthApiUrl;

    public SimpleJwtTokenProvider(String baseAuthApiUrl) {
        this.baseAuthApiUrl = baseAuthApiUrl;
    }

    public SimpleJwtTokenProvider(String baseAuthApiUrl, JwtTokenBundle jwtTokenBundle) {
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.token = jwtTokenBundle.getToken();
        this.refreshToken = jwtTokenBundle.getRefreshToken();
    }
}
