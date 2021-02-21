package io.rocketbase.commons.service.forgot;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.FeedbackActionService;

public interface AppUserForgotPasswordService extends FeedbackActionService {

    /**
     * @param forgotPassword username or email to look for
     * @param baseUrl        will be used as baseUrl for emails
     * @return in any case an ExpirationInfo - whether the user was found or not - to hide user info from scrapers
     */
    ExpirationInfo<Void> requestPasswordReset(ForgotPasswordRequest forgotPassword, String baseUrl);

    AppUserEntity resetPassword(PerformPasswordResetRequest performPasswordReset);

}
