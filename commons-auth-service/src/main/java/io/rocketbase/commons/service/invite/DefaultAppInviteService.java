package io.rocketbase.commons.service.invite;

import io.rocketbase.commons.config.AuthProperties;
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
import java.util.Optional;

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
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public AppInviteEntity createInvite(InviteRequest request, String baseUrl) throws BadRequestException {

        AppInviteEntity entity = appInvitePersistenceService.initNewInstance();
        entity.setSystemRefId(request.getSystemRefId());
        entity.setInvitor(request.getInvitor());
        entity.setMessage(request.getMessage());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());
        entity.setCapabilityIds(request.getCapabilityIds());
        entity.setGroupIds(request.getGroupIds());
        entity.setKeyValues(request.getKeyValues());
        entity.setTeamInvite(request.getTeamInvite());
        entity.setExpiration(Instant.now().plus(authProperties.getInviteExpiration()));

        entity = appInvitePersistenceService.save(entity);

        applicationEventPublisher.publishEvent(new InviteEvent(this, entity, CREATE));

        emailService.sentInviteEmail(entity, buildActionUrl(baseUrl, ActionType.INVITE, entity.getId(), request.getInviteUrl()));
        return entity;
    }

    @Override
    public AppInviteEntity verifyInvite(Long inviteId) throws VerificationException, NotFoundException {
        AppInviteEntity invite = appInvitePersistenceService.findById(inviteId).orElseThrow(NotFoundException::new);
        if (!invite.getExpiration().isAfter(Instant.now())) {
            throw new VerificationException("inviteId");
        }
        applicationEventPublisher.publishEvent(new InviteEvent(this, invite, VERIFY));

        return invite;
    }

    @Override
    public AppUserEntity confirmInvite(ConfirmInviteRequest request) throws RegistrationException, VerificationException {
        AppInviteEntity inviteEntity = verifyInvite(request.getInviteId());
        // validate username, password + email
        validationService.registrationIsValid(request.getUsername(), request.getPassword(), request.getEmail());

        // TODO: Memebership needs to get handeled here?
        AppUserCreate userCreate = AppUserCreate.builder()
                .username(request.getUsername().toLowerCase())
                .password(request.getPassword())
                .systemRefId(inviteEntity.getSystemRefId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .keyValues(inviteEntity.getKeyValues())
                .capabilityIds(inviteEntity.getCapabilityIds())
                .groupIds(inviteEntity.getGroupIds())
                .enabled(true)
                .build();

        AppUserEntity appUser = appUserService.initializeUser(userCreate);

        applicationEventPublisher.publishEvent(new InviteEvent(this, inviteEntity, appUser));

        appInvitePersistenceService.delete(inviteEntity.getId());
        return appUser;
    }

    @Override
    public Page<AppInviteEntity> findAll(QueryAppInvite query, Pageable pageable) {
        return appInvitePersistenceService.findAll(query, pageable);
    }

    @Override
    public Optional<AppInviteEntity> findById(Long id) {
        return appInvitePersistenceService.findById(id);
    }

    @Override
    public void deleteInvite(Long inviteId) {
        AppInviteEntity entity = appInvitePersistenceService.findById(inviteId).orElseThrow(NotFoundException::new);
        appInvitePersistenceService.delete(entity.getId());
    }
}
