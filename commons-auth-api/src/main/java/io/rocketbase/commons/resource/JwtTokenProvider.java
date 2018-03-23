package io.rocketbase.commons.resource;

import io.rocketbase.commons.dto.JwtTokenBundle;

public interface JwtTokenProvider {

    JwtTokenProvider init(JwtTokenBundle jwtTokenBundle);

    String getToken();

    void setToken(String token);

    String getRefreshToken();

    String getRefreshTokenEndpoint();
}
