package io.rocketbase.commons.adapters;

public interface JwtTokenProvider {

    String getToken();

    void setToken(String token);

    String getRefreshToken();

    String getBaseAuthApiUrl();
}
