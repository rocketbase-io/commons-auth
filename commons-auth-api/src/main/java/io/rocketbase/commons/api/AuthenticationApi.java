package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;

public interface AuthenticationApi {

    AppUserRead getAuthenticated();

    void changePassword(PasswordChangeRequest passwordChange);

    void updateProfile(UpdateProfileRequest updateProfile);

}
