package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;

public interface ForgotPasswordApi {

    ExpirationInfo<AppUserRead> forgotPassword(ForgotPasswordRequest forgotPassword);

    void resetPassword(PerformPasswordResetRequest performPasswordReset);

}
