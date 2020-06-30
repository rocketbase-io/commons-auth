package io.rocketbase.commons.api;

import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;

public interface LoginApi {

    LoginResponse login(LoginRequest login);

    String getNewAccessToken(String refreshToken);

    default void refreshAccessToken(JwtTokenProvider tokenProvider) {
        String accessToken = getNewAccessToken(tokenProvider.getRefreshToken());
        tokenProvider.setToken(accessToken);
    }

}
