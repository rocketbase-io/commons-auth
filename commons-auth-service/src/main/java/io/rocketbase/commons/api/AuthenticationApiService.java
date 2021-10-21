package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.dto.authentication.EmailChangeRequest;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UsernameChangeRequest;
import io.rocketbase.commons.event.UpdateProfileEvent;
import io.rocketbase.commons.event.UpdateSettingEvent;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
import io.rocketbase.commons.security.CommonsPrincipal;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.change.ChangeAppUserWithConfirmService;
import io.rocketbase.commons.service.user.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class AuthenticationApiService implements AuthenticationApi, BaseApiService {

    private final AppUserService appUserService;
    private final AppUserConverter userConverter;
    private final ChangeAppUserWithConfirmService changeAppUserWithConfirmService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JwtTokenService jwtTokenService;

    @Override
    public AppUserToken getAuthenticated() {
        CommonsPrincipal principal = getCurrentPrincipal();
        AppUserEntity entity = appUserService.findById(principal.getId())
                .orElseThrow(NotFoundException::new);

        return userConverter.toToken(entity);
    }

    @Override
    public JwtTokenBundle changePassword(PasswordChangeRequest passwordChange) {
        CommonsPrincipal principal = getCurrentPrincipal();
        AppUserEntity entity = appUserService.performUpdatePassword(principal.getId(), passwordChange);
        return jwtTokenService.generateTokenBundle(userConverter.toToken(entity));
    }

    @Override
    public AppUserRead changeUsername(UsernameChangeRequest usernameChange) {
        CommonsPrincipal principal = getCurrentPrincipal();
        AppUserEntity entity = appUserService.changeUsername(principal.getId(), usernameChange.getNewUsername());
        return userConverter.fromEntity(entity);
    }

    @Override
    public ExpirationInfo<AppUserRead> changeEmail(EmailChangeRequest emailChange) {
        CommonsPrincipal principal = getCurrentPrincipal();
        ExpirationInfo<AppUserEntity> expirationInfo = changeAppUserWithConfirmService.handleEmailChangeRequest(principal.getId(), emailChange, getBaseUrl());
        return ExpirationInfo.<AppUserRead>builder()
                .expires(expirationInfo.getExpires())
                .detail(userConverter.fromEntity(expirationInfo.getDetail()))
                .build();
    }

    @Override
    public AppUserRead verifyEmail(String verification) {
        AppUserEntity entity = changeAppUserWithConfirmService.confirmEmailChange(verification);
        return userConverter.fromEntity(entity);
    }

    @Override
    public AppUserRead updateProfile(UserProfile userProfile) {
        AppUserEntity entity = appUserService.patch(getCurrentPrincipal().getId(), AppUserUpdate.builder().profile(userProfile).build());
        applicationEventPublisher.publishEvent(new UpdateProfileEvent(this, entity));
        return userConverter.fromEntity(entity);
    }

    @Override
    public AppUserRead updateSetting(UserSetting userSetting) {
        AppUserEntity entity = appUserService.patch(getCurrentPrincipal().getId(), AppUserUpdate.builder().setting(userSetting).build());
        applicationEventPublisher.publishEvent(new UpdateSettingEvent(this, entity));
        return userConverter.fromEntity(entity);
    }

    protected CommonsPrincipal getCurrentPrincipal() {
        CommonsPrincipal principal = CommonsPrincipal.getCurrent();
        if (principal == null) {
            throw new RuntimeException("user not found");
        }
        return principal;
    }
}
