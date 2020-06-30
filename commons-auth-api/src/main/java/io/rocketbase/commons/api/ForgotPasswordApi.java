package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;

public interface ForgotPasswordApi {

    void forgotPassword(ForgotPasswordRequest forgotPassword);

    void resetPassword(PerformPasswordResetRequest performPasswordReset);

}
