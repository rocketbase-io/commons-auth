package io.rocketbase.commons.service.forgot;

import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.FeedbackActionService;

public interface AppUserForgotPasswordService extends FeedbackActionService {

    AppUser requestPasswordReset(ForgotPasswordRequest forgotPassword, String baseUrl);

    AppUser resetPassword(PerformPasswordResetRequest performPasswordReset);

}
