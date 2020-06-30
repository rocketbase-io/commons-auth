package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.registration.RegistrationService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrationServiceApi implements RegistrationApi, BaseServiceApi {

    private final RegistrationService registrationService;
    private final AppUserConverter converter;
    private final JwtTokenService jwtTokenService;

    @Override
    public AppUserRead register(RegistrationRequest registration) {
        AppUserEntity entity = registrationService.register(registration, getBaseUrl());
        return converter.fromEntity(entity);
    }

    @Override
    public JwtTokenBundle verify(String verification) {
        AppUserEntity appUserEntity = registrationService.verifyRegistration(verification);
        return jwtTokenService.generateTokenBundle(appUserEntity);
    }
}
