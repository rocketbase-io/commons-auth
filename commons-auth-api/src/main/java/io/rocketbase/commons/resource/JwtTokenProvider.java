package io.rocketbase.commons.resource;

public interface JwtTokenProvider {

    String getToken();

    void setToken(String token);

    String getRefreshToken();

    String getRefreshTokenEndpoint();
}
