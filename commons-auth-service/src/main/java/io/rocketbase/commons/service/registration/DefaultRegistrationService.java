package io.rocketbase.commons.service.registration;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.event.RegistrationEvent;
import io.rocketbase.commons.exception.EmailDeliveryException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.service.SimpleTokenService;
import io.rocketbase.commons.service.SimpleTokenService.Token;
import io.rocketbase.commons.service.email.AuthEmailService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.user.AppUserTokenService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static io.rocketbase.commons.event.RegistrationEvent.RegistrationProcessType.REGISTER;
import static io.rocketbase.commons.event.RegistrationEvent.RegistrationProcessType.VERIFIED;
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
    private AppUserTokenService appUserTokenService;

    @Resource
    private AuthEmailService emailService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public ExpirationInfo<AppUserEntity> register(RegistrationRequest registration, String baseUrl) {
        AppUserEntity entity = appUserService.registerUser(registration);

        ExpirationInfo<AppUserEntity> expirationInfo = new ExpirationInfo<>(null, entity);
        if (registrationProperties.isVerification()) {
            try {
                expirationInfo.setExpires(Instant.now().plus(registrationProperties.getVerificationExpiration(), ChronoUnit.MINUTES));

                String token = SimpleTokenService.generateToken(registration.getUsername(), registrationProperties.getVerificationExpiration());
                appUserService.updateKeyValues(entity.getUsername(), ImmutableMap.of(REGISTRATION_KV, token));

                emailService.sentRegistrationEmail(entity, buildActionUrl(baseUrl, ActionType.VERIFICATION_REGISTRATION, token, registration.getVerificationUrl()));
            } catch (Exception e) {
                log.error("couldn't sent email. please check your configuration. registration has been deleted {}", e.getMessage());
                appUserService.delete(entity.getId());
                throw new EmailDeliveryException();
            }
        }
        applicationEventPublisher.publishEvent(new RegistrationEvent(this, entity, REGISTER));

        return expirationInfo;
    }

    public AppUserToken verifyRegistration(String verification) throws VerificationException {
        Token token = SimpleTokenService.parseToken(verification);
        if (!token.isValid()) {
            throw new VerificationException("verification");
        }
        AppUserEntity entity = appUserService.getByUsername(token.getUsername());
        String dbRegistrationToken = entity.getKeyValues().getOrDefault(REGISTRATION_KV, null);

        if (!verification.equals(dbRegistrationToken)) {
            throw new VerificationException("verification");
        }

        appUserService.processRegistrationVerification(token.getUsername());

        applicationEventPublisher.publishEvent(new RegistrationEvent(this, entity, VERIFIED));

        return appUserTokenService.lookup(entity);
    }


}
