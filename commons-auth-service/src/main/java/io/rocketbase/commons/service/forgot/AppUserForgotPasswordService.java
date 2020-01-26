package io.rocketbase.commons.service.forgot;

import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.FeedbackActionService;

public interface AppUserForgotPasswordService extends FeedbackActionService {

    AppUserEntity requestPasswordReset(ForgotPasswordRequest forgotPassword, String baseUrl);

    AppUserEntity resetPassword(PerformPasswordResetRequest performPasswordReset);

}
