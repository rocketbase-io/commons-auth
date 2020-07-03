package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.user.AppUserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginApiService implements LoginApi {

    private final LoginService loginService;

    private final JwtTokenService jwtTokenService;

    private final AppUserService appUserService;

    @Override
    public LoginResponse login(LoginRequest login) {
        return loginService.performLogin(login.getUsername(), login.getPassword());
    }

    @Override
    public String getNewAccessToken(String refreshToken) {
        AppUserToken appUserToken = jwtTokenService.parseToken(refreshToken);
        if (!appUserToken.hasRole(JwtTokenService.REFRESH_TOKEN)) {
            throw new RuntimeException("need a valid refresh-token!");
        }
        return jwtTokenService.generateAccessToken(appUserService.getByUsername(appUserToken.getUsername()));
    }
}
