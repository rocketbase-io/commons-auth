package io.rocketbase.commons.service.invite;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.event.InviteEvent;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.AppInvitePersistenceService;
import io.rocketbase.commons.service.email.AuthEmailService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static io.rocketbase.commons.event.InviteEvent.InviteProcessType.CREATE;
import static io.rocketbase.commons.event.InviteEvent.InviteProcessType.VERIFY;

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
    private AuthEmailService emailService;

    @Resource
    private AppInviteConverter appInviteConverter;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    public AppInviteEntity createInvite(InviteRequest request, String baseUrl) throws BadRequestException {
        AppInviteEntity dto = appInvitePersistenceService.initNewInstance();
        appInviteConverter.updateEntity(dto, request);
        dto.setExpiration(Instant.now().plus(authProperties.getInviteExpiration(), ChronoUnit.MINUTES));
        AppInviteEntity entity = appInvitePersistenceService.save(dto);

        applicationEventPublisher.publishEvent(new InviteEvent(this, entity, CREATE));

        emailService.sentInviteEmail(entity, buildActionUrl(baseUrl, ActionType.INVITE, entity.getId(), request.getInviteUrl()));
        return entity;
    }

    @Override
    public AppInviteEntity verifyInvite(String inviteId) throws VerificationException, NotFoundException {
        AppInviteEntity inviteEntity = appInvitePersistenceService.findById(inviteId).orElseThrow(NotFoundException::new);
        if (!inviteEntity.getExpiration().isAfter(Instant.now())) {
            throw new VerificationException("inviteId");
        }
        applicationEventPublisher.publishEvent(new InviteEvent(this, inviteEntity, VERIFY));

        return inviteEntity;
    }

    @Override
    public AppUserEntity confirmInvite(ConfirmInviteRequest request) throws RegistrationException, VerificationException {
        AppInviteEntity inviteEntity = verifyInvite(request.getInviteId());
        // validate username, password + email
        validationService.registrationIsValid(request.getUsername(), request.getPassword(), request.getEmail());

        AppUserCreate userCreate = AppUserCreate.builder()
                .username(request.getUsername().toLowerCase())
                .password(request.getPassword())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .keyValues(inviteEntity.getKeyValues())
                .roles(inviteEntity.getRoles())
                .build();

        AppUserEntity appUserEntity = appUserService.initializeUser(userCreate);

        applicationEventPublisher.publishEvent(new InviteEvent(this, inviteEntity, appUserEntity));

        appInvitePersistenceService.delete(inviteEntity);
        return appUserEntity;
    }

    @Override
    public Page<AppInviteEntity> findAll(QueryAppInvite query, Pageable pageable) {
        return appInvitePersistenceService.findAll(query, pageable);
    }

    @Override
    public void deleteInvite(String inviteId) {
        appInvitePersistenceService.delete(appInvitePersistenceService.findById(inviteId).orElseThrow(NotFoundException::new));
    }
}
