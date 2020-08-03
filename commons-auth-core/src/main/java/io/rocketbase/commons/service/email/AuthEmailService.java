package io.rocketbase.commons.service.email;

import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;

public interface AuthEmailService {

    void sentRegistrationEmail(AppUserReference user, String verificationUrl);

    void sentForgotPasswordEmail(AppUserReference user, String verificationUrl);

    void sentInviteEmail(AppInviteEntity invite, String verificationUrl);

    void sentChangeEmailAddressEmail(AppUserReference user, String newEmailAddress, String verificationUrl);

}
