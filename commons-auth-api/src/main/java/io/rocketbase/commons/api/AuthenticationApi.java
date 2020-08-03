package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.EmailChangeRequest;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import io.rocketbase.commons.dto.authentication.UsernameChangeRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.UsernameValidationException;

public interface AuthenticationApi {

    AppUserRead getAuthenticated();

    void changePassword(PasswordChangeRequest passwordChange);

    AppUserRead changeUsername(UsernameChangeRequest usernameChange) throws UsernameValidationException;

    ExpirationInfo<AppUserRead> changeEmail(EmailChangeRequest emailChange) throws EmailValidationException;

    AppUserRead verifyEmail(String verification);

    void updateProfile(UpdateProfileRequest updateProfile);

}
