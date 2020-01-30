package io.rocketbase.commons.service.invite;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.AppInvitePersistenceService;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class DefaultAppInviteService implements AppInviteService {

    @Getter
    final AuthProperties authProperties;

    @Resource
    protected AppInvitePersistenceService<AppInviteEntity> appInvitePersistenceService;

    @Resource
    private AppUserService appUserService;

    @Resource
    private ValidationService validationService;

    @Resource
    private EmailService emailService;

    @Resource
    private AppInviteConverter appInviteConverter;


    @Override
    public AppInviteEntity createInvite(InviteRequest request, String baseUrl) throws BadRequestException {
        AppInviteEntity dto = appInvitePersistenceService.initNewInstance();
        appInviteConverter.updateEntity(dto, request);
        dto.setExpiration(Instant.now().plus(authProperties.getInviteExpiration(), ChronoUnit.MINUTES));
        AppInviteEntity entity = appInvitePersistenceService.save(dto);

        emailService.sentInviteEmail(entity, buildActionUrl(baseUrl, ActionType.INVITE, entity.getId(), request.getInviteUrl()));
        return entity;
    }

    @Override
    public AppInviteEntity verifyInvite(String inviteId) throws VerificationException, NotFoundException {
        AppInviteEntity inviteEntity = appInvitePersistenceService.findById(inviteId).orElseThrow(NotFoundException::new);
        if (!inviteEntity.getExpiration().isAfter(Instant.now())) {
            throw new VerificationException();
        }
        return inviteEntity;
    }

    @Override
    public AppUserEntity confirmInvite(ConfirmInviteRequest request) throws RegistrationException, VerificationException {
        AppInviteEntity inviteEntity = verifyInvite(request.getInviteId());
        // validate username, password + email
        validationService.registrationIsValid(request.getUsername(), request.getPassword(), request.getEmail());
        AppUserEntity appUserEntity = appUserService.initializeUser(request.getUsername(), request.getPassword(), request.getEmail(), inviteEntity.getRoles());
        appUserEntity = appUserService.updateProfile(appUserEntity.getUsername(), request.getFirstName(), request.getLastName(), appUserEntity.getAvatar(), inviteEntity.getKeyValues());
        appInvitePersistenceService.delete(inviteEntity);
        return appUserEntity;
    }
}
