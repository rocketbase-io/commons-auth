package io.rocketbase.commons.service.registration;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.event.RegistrationEvent;
import io.rocketbase.commons.event.VerificationEvent;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.SimpleTokenService;
import io.rocketbase.commons.service.SimpleTokenService.Token;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.user.AppUserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;

import static io.rocketbase.commons.service.user.DefaultAppUserService.REGISTRATION_KV;

@Slf4j
@RequiredArgsConstructor
public class DefaultRegistrationService implements RegistrationService {

    @Getter
    final AuthProperties authProperties;
    final RegistrationProperties registrationProperties;

    @Resource
    private AppUserService appUserService;

    @Resource
    private EmailService emailService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AppUserEntity register(RegistrationRequest registration, String baseUrl) {
        AppUserEntity entity = appUserService.registerUser(registration);
        if (registrationProperties.isVerification()) {
            try {
                String token = SimpleTokenService.generateToken(registration.getUsername(), registrationProperties.getVerificationExpiration());
                appUserService.updateKeyValues(entity.getUsername(), ImmutableMap.of(REGISTRATION_KV, token));

                emailService.sentRegistrationEmail(entity, buildActionUrl(baseUrl, ActionType.VERIFICATION, token, registration.getVerificationUrl()));
            } catch (Exception e) {
                log.error("couldn't sent email. please check your configuration. {}", e.getMessage());
                appUserService.delete(entity);
                throw e;
            }
        }
        applicationEventPublisher.publishEvent(new RegistrationEvent(this, entity));

        return entity;
    }

    public AppUserEntity verifyRegistration(String verification) {
        Token token = SimpleTokenService.parseToken(verification);
        if (!token.isValid()) {
            throw new VerificationException();
        }
        AppUserEntity entity = appUserService.getByUsername(token.getUsername());
        String dbRegistrationToken = entity.getKeyValues().getOrDefault(REGISTRATION_KV, null);

        if (!verification.equals(dbRegistrationToken)) {
            throw new VerificationException();
        }

        appUserService.processRegistrationVerification(token.getUsername());

        applicationEventPublisher.publishEvent(new VerificationEvent(this, entity));

        return entity;
    }


}
