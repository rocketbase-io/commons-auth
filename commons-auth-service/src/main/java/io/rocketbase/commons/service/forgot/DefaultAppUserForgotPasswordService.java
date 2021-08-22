package io.rocketbase.commons.service.forgot;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.event.PasswordEvent;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.SimpleTokenService;
import io.rocketbase.commons.service.SimpleTokenService.Token;
import io.rocketbase.commons.service.email.AuthEmailService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.util.Nulls;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static io.rocketbase.commons.event.PasswordEvent.PasswordProcessType.PROCEED_RESET;
import static io.rocketbase.commons.event.PasswordEvent.PasswordProcessType.REQUEST_RESET;
import static io.rocketbase.commons.service.user.DefaultAppUserService.FORGOTPW_KV;

@Slf4j
@RequiredArgsConstructor
public class DefaultAppUserForgotPasswordService implements AppUserForgotPasswordService {

    @Getter
    final AuthProperties authProperties;

    @Resource
    private AppUserService appUserService;

    @Resource
    private AuthEmailService emailService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public ExpirationInfo<Void> requestPasswordReset(ForgotPasswordRequest forgotPassword, String baseUrl) {
        AppUserEntity appUser = null;
        if (StringUtils.hasText(forgotPassword.getUsername())) {
            appUser = appUserService.getByUsername(forgotPassword.getUsername());
        }
        if (StringUtils.hasText(forgotPassword.getEmail()) && appUser == null) {
            appUser = appUserService.findByEmail(forgotPassword.getEmail().toLowerCase()).orElse(null);
        }
        Instant expires = Instant.now().plus(authProperties.getPasswordResetExpiration(), ChronoUnit.MINUTES);
        ExpirationInfo<Void> info = ExpirationInfo.<Void>builder()
                .expires(expires)
                .build();

        if (appUser == null || !appUser.isEnabled()) {
            log.debug("requested password reset for unkown username: {} / email: {}", forgotPassword.getUsername(), forgotPassword.getEmail());
            return info;
        }

        String token = SimpleTokenService.generateToken(appUser.getUsername(), authProperties.getPasswordResetExpiration());
        appUserService.updateKeyValues(appUser.getUsername(), ImmutableMap.of(FORGOTPW_KV, token));

        emailService.sentForgotPasswordEmail(appUser, buildActionUrl(baseUrl, ActionType.PASSWORD_RESET, token, Nulls.notEmpty(forgotPassword.getResetPasswordUrl(), forgotPassword.getVerificationUrl())));

        applicationEventPublisher.publishEvent(new PasswordEvent(this, appUser, REQUEST_RESET));

        return info;
    }

    @Override
    public AppUserEntity resetPassword(PerformPasswordResetRequest performPasswordReset) {
        Token token = SimpleTokenService.parseToken(performPasswordReset.getVerification());
        if (!token.isValid()) {
            throw new VerificationException("verification");
        }
        AppUserEntity user = appUserService.getByUsername(token.getUsername());
        if (user == null || !user.isEnabled()) {
            throw new VerificationException("verification");
        }
        String dbForgotToken = user.getKeyValues().getOrDefault(FORGOTPW_KV, null);
        if (!performPasswordReset.getVerification().equals(dbForgotToken)) {
            throw new VerificationException("verification");
        }

        appUserService.updatePasswordUnchecked(user.getUsername(), performPasswordReset.getPassword());

        applicationEventPublisher.publishEvent(new PasswordEvent(this, user, PROCEED_RESET));

        return user;
    }

}
