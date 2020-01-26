package io.rocketbase.commons.service.auth;

import io.rocketbase.commons.dto.authentication.LoginResponse;

public interface LoginService {

    LoginResponse performLogin(String username, String password);
}
