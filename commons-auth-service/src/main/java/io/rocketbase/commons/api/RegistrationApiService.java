package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.registration.RegistrationService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrationApiService implements RegistrationApi, BaseApiService {

    private final RegistrationService registrationService;
    private final AppUserConverter converter;
    private final JwtTokenService jwtTokenService;

    @Override
    public ExpirationInfo<AppUserRead> register(RegistrationRequest registration) {
        ExpirationInfo<AppUserEntity> expirationInfo = registrationService.register(registration, getBaseUrl());
        return ExpirationInfo.<AppUserRead>builder()
                .expires(expirationInfo.getExpires())
                .detail(converter.toRead(expirationInfo.getDetail()))
                .build();
    }

    @Override
    public JwtTokenBundle verify(String verification) {
        AppUserToken token = registrationService.verifyRegistration(verification);
        return jwtTokenService.generateTokenBundle(token);
    }
}
