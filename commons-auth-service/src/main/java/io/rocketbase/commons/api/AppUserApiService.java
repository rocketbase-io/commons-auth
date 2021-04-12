package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appuser.*;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.invite.AppInviteService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class AppUserApiService implements AppUserApi, BaseApiService {

    private final AppUserService appUserService;
    private final AppUserConverter userConverter;

    private final ValidationService validationService;

    private final AppInviteService appInviteService;
    private final AppInviteConverter inviteConverter;

    @Override
    public PageableResult<AppUserRead> find(QueryAppUser query, Pageable pageable) {
        Page<AppUserEntity> page = appUserService.findAll(query, pageable);
        return PageableResult.contentPage(userConverter.fromEntities(page.getContent()), page);
    }

    @Override
    public Optional<AppUserRead> findById(String id) {
        Optional<AppUserEntity> optional = appUserService.findById(id);
        return optional.isPresent() ? Optional.of(userConverter.fromEntity(optional.get())) : Optional.empty();
    }

    @Override
    public AppUserRead create(AppUserCreate create) {
        validationService.registrationIsValid(create.getUsername(), create.getPassword(), create.getEmail());

        AppUserEntity entity = appUserService.initializeUser(create);
        return userConverter.fromEntity(entity);
    }

    @Override
    public AppUserRead resetPassword(String usernameOrId, AppUserResetPassword reset) {
        validationService.passwordIsValid("resetPassword", reset.getResetPassword());

        AppUserEntity entity = appUserService.updatePasswordUnchecked(usernameOrId, reset.getResetPassword());
        return userConverter.fromEntity(entity);
    }

    @Override
    public AppUserRead update(String usernameOrId, AppUserUpdate update) {
        AppUserEntity entity = appUserService.save(usernameOrId, update);
        return userConverter.fromEntity(entity);
    }

    @Override
    public AppUserRead patch(String usernameOrId, AppUserUpdate update) {
        AppUserEntity entity = appUserService.patch(usernameOrId, update);
        return userConverter.fromEntity(entity);
    }

    @Override
    public void delete(String id) {
        appUserService.delete(id);
    }

    @Override
    public AppInviteRead invite(InviteRequest inviteRequest) {
        AppInviteEntity invite = appInviteService.createInvite(inviteRequest, getBaseUrl());
        return inviteConverter.fromEntity(invite);
    }
}
