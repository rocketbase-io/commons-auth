package io.rocketbase.commons.service.invite;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.AppInvitePersistenceService;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.validation.ValidationErrorCodeService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.time.Instant;

@RequiredArgsConstructor
public class DefaultInviteUserService implements InviteUserService {

    @Getter
    final AuthProperties authProperties;

    @Resource
    private AppUserService appUserService;

    @Resource
    protected AppInvitePersistenceService<AppInviteEntity> appInvitePersistenceService;

    @Resource
    private ValidationService validationService;

    @Resource
    private ValidationErrorCodeService validationErrorCodeService;

    @Resource
    private EmailService emailService;

    @Resource
    private AppInviteConverter appInviteConverter;


    @Override
    public AppInviteEntity createInvite(InviteRequest request, String baseUrl) throws EmailValidationException, BadRequestException {
        Page<AppInviteEntity> foundByEmail = appInvitePersistenceService.findAll(QueryAppInvite.builder().email(request.getEmail()).build(), PageRequest.of(0, 1));
        if (!foundByEmail.isEmpty()) {
            throw new EmailValidationException(validationErrorCodeService.emailErrors(EmailErrorCodes.ALREADY_TAKEN));
        }

        AppInviteEntity dto = appInvitePersistenceService.initNewInstance();
        appInviteConverter.updateEntity(dto, request);
        dto.setExpiration(Instant.now().plusSeconds(authProperties.getInviteExpiration() * 60));
        AppInviteEntity entity = appInvitePersistenceService.save(dto);

        emailService.sentInviteEmail(entity, buildActionUrl(baseUrl, ActionType.INVITE, entity.getId(), request.getInviteUrl()));
        return entity;
    }

    @Override
    public AppInviteEntity verifyInvite(String inviteId) throws VerificationException {
        AppInviteEntity inviteEntity = appInvitePersistenceService.findById(inviteId).orElseThrow(VerificationException::new);
        if (!inviteEntity.getExpiration().isAfter(Instant.now())) {
            throw new VerificationException();
        }
        return inviteEntity;
    }

    @Override
    public AppUserEntity confirmInvite(String inviteId, ConfirmInviteRequest request) throws RegistrationException, VerificationException {
        AppInviteEntity inviteEntity = verifyInvite(inviteId);
        // validate username, password + email
        validationService.registrationIsValid(request.getUsername(), request.getPassword(), request.getEmail());
        AppUserEntity appUserEntity = appUserService.initializeUser(request.getUsername(), request.getPassword(), request.getEmail(), inviteEntity.getRoles());
        appUserEntity = appUserService.updateProfile(appUserEntity.getUsername(), request.getFirstName(), request.getLastName(),  appUserEntity.getAvatar(), inviteEntity.getKeyValues());
        appInvitePersistenceService.delete(inviteEntity);
        return appUserEntity;
    }
}
