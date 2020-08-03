package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.EmailChangeRequest;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import io.rocketbase.commons.dto.authentication.UsernameChangeRequest;

public interface AuthenticationApi {

    AppUserRead getAuthenticated();

    void changePassword(PasswordChangeRequest passwordChange);

    AppUserRead changeUsername(UsernameChangeRequest usernameChange);

    ExpirationInfo<AppUserRead> changeEmail(EmailChangeRequest emailChange);

    AppUserRead verifyEmail(String verification);

    void updateProfile(UpdateProfileRequest updateProfile);

}
