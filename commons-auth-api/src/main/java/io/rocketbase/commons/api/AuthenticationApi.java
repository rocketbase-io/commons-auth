package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.EmailChangeRequest;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UsernameChangeRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.UsernameValidationException;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;

public interface AuthenticationApi {
    AppUserToken getAuthenticated();

    void changePassword(PasswordChangeRequest passwordChange);

    AppUserRead changeUsername(UsernameChangeRequest usernameChange) throws UsernameValidationException;

    ExpirationInfo<AppUserRead> changeEmail(EmailChangeRequest emailChange) throws EmailValidationException;

    AppUserRead verifyEmail(String verification);

    AppUserRead updateProfile(UserProfile userProfile);

    AppUserRead updateSetting(UserSetting userSetting);

}
