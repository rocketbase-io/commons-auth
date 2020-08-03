package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.service.forgot.AppUserForgotPasswordService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ForgotPasswordApiService implements ForgotPasswordApi, BaseApiService {

    private final AppUserForgotPasswordService forgotPasswordService;

    @Override
    public ExpirationInfo forgotPassword(ForgotPasswordRequest forgotPassword) {
        return forgotPasswordService.requestPasswordReset(forgotPassword, getBaseUrl());
    }

    @Override
    public void resetPassword(PerformPasswordResetRequest performPasswordReset) {
        forgotPasswordService.resetPassword(performPasswordReset);
    }
}
