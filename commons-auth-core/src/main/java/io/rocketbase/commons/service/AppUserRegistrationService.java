package io.rocketbase.commons.service;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.event.RegistrationEvent;
import io.rocketbase.commons.event.VerificationEvent;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.email.EmailService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;

import static io.rocketbase.commons.service.AppUserService.REGISTRATION_KV;

@RequiredArgsConstructor
public class AppUserRegistrationService implements FeedbackActionService {

    @Getter
    final AuthProperties authProperties;
    final RegistrationProperties registrationProperties;

    @Resource
    private AppUserService appUserService;

    @Resource
    private EmailService emailService;

    @Resource
    private TokenizerService tokenizerService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;


    public AppUser register(RegistrationRequest registration, String baseUrl) {
        AppUser search = appUserService.getByUsername(registration.getUsername().toLowerCase());
        boolean emailUsed = appUserService.findByEmail(registration.getEmail().toLowerCase()).isPresent();
        if (search != null || emailUsed) {
            throw new RegistrationException(search != null, emailUsed);
        }
        AppUser entity = appUserService.registerUser(registration);

        if (registrationProperties.isVerification()) {
            try {
                String token = tokenizerService.generateToken(entity.getUsername(), null, registrationProperties.getVerificationExpiration());
                appUserService.updateKeyValues(entity.getUsername(), ImmutableMap.of(REGISTRATION_KV, token));

                emailService.sentRegistrationEmail(entity, buildActionUrl(baseUrl, ActionType.VERIFICATION, token));
            } catch (Exception e) {
                appUserService.delete(entity);
                throw e;
            }
        }
        applicationEventPublisher.publishEvent(new RegistrationEvent(this, entity));

        return entity;
    }

    public AppUser verifyRegistration(String verification) {
        TokenizerService.Token token = tokenizerService.parseToken(verification);
        if (!token.isValid()) {
            throw new VerificationException();
        }
        AppUser entity = appUserService.getByUsername(token.getUsername());
        String dbRegistrationToken = entity.getKeyValues().getOrDefault(REGISTRATION_KV, null);

        if (!verification.equals(dbRegistrationToken)) {
            throw new VerificationException();
        }

        appUserService.processRegistrationVerification(token.getUsername());

        applicationEventPublisher.publishEvent(new VerificationEvent(this, entity));

        return entity;
    }


}
