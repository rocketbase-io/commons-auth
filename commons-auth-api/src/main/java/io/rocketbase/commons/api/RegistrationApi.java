package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.registration.RegistrationRequest;

public interface RegistrationApi {

    ExpirationInfo<AppUserRead> register(RegistrationRequest registration);

    JwtTokenBundle verify(String verification);

}
