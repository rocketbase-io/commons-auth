package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;

public interface ForgotPasswordApi {

    /**
     * @return in any case an ExpirationInfo - whether the user was found or not - to hide user info from scrapers
     */
    ExpirationInfo<Void> forgotPassword(ForgotPasswordRequest forgotPassword);

    AppUserRead resetPassword(PerformPasswordResetRequest performPasswordReset);

}
