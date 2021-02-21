package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.forgot.AppUserForgotPasswordService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ForgotPasswordApiService implements ForgotPasswordApi, BaseApiService {

    private final AppUserForgotPasswordService forgotPasswordService;
    private final AppUserConverter appUserConverter;

    @Override
    public ExpirationInfo<Void> forgotPassword(ForgotPasswordRequest forgotPassword) {
        return forgotPasswordService.requestPasswordReset(forgotPassword, getBaseUrl());
    }

    @Override
    public AppUserRead resetPassword(PerformPasswordResetRequest performPasswordReset) {
        AppUserEntity entity = forgotPasswordService.resetPassword(performPasswordReset);
        return appUserConverter.fromEntity(entity);
    }
}
