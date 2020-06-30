package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.CommonsPrincipal;
import io.rocketbase.commons.service.user.AppUserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationServiceApi implements AuthenticationApi {

    private final AppUserService appUserService;
    private final AppUserConverter userConverter;

    @Override
    public AppUserRead getAuthenticated() {
        CommonsPrincipal principal = getCurrentPrincipal();
        AppUserEntity entity = appUserService.findById(principal.getId())
                .orElseThrow(NotFoundException::new);

        return userConverter.fromEntity(entity);
    }

    @Override
    public void changePassword(PasswordChangeRequest passwordChange) {
        CommonsPrincipal principal = getCurrentPrincipal();
        appUserService.performUpdatePassword(principal.getId(), passwordChange);
    }

    @Override
    public void updateProfile(UpdateProfileRequest updateProfile) {
        CommonsPrincipal principal = getCurrentPrincipal();
        appUserService.updateProfile(principal.getId(), updateProfile);
    }

    protected CommonsPrincipal getCurrentPrincipal() {
        CommonsPrincipal principal = CommonsPrincipal.getCurrent();
        if (principal == null) {
            throw new RuntimeException("user not found");
        }
        return principal;
    }
}
