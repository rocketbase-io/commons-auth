package io.rocketbase.commons.service.forgot;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.event.ForgotPasswordEvent;
import io.rocketbase.commons.event.ResetPasswordEvent;
import io.rocketbase.commons.exception.UnknownUserException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.SimpleTokenService;
import io.rocketbase.commons.service.SimpleTokenService.Token;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.user.AppUserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;

import static io.rocketbase.commons.service.user.DefaultAppUserService.FORGOTPW_KV;

@RequiredArgsConstructor
public class DefaultAppUserForgotPasswordService implements AppUserForgotPasswordService {

    @Getter
    final AuthProperties authProperties;

    @Resource
    private AppUserService appUserService;

    @Resource
    private EmailService emailService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public AppUserEntity requestPasswordReset(ForgotPasswordRequest forgotPassword, String baseUrl) {
        AppUserEntity appUser = null;
        if (forgotPassword.getUsername() != null) {
            appUser = appUserService.getByUsername(forgotPassword.getUsername());
        } else if (forgotPassword.getEmail() != null) {
            appUser = appUserService.findByEmail(forgotPassword.getEmail().toLowerCase()).orElseGet(null);
        }
        if (appUser == null || !appUser.isEnabled()) {
            throw new UnknownUserException();
        }
        String token = SimpleTokenService.generateToken(appUser.getUsername(), authProperties.getPasswordResetExpiration());
        appUserService.updateKeyValues(appUser.getUsername(), ImmutableMap.of(FORGOTPW_KV, token));

        emailService.sentForgotPasswordEmail(appUser, buildActionUrl(baseUrl, ActionType.PASSWORD_RESET, token, forgotPassword.getVerificationUrl()));

        applicationEventPublisher.publishEvent(new ForgotPasswordEvent(this, appUser));

        return appUser;
    }

    @Override
    public AppUserEntity resetPassword(PerformPasswordResetRequest performPasswordReset) {
        Token token = SimpleTokenService.parseToken(performPasswordReset.getVerification());
        if (!token.isValid()) {
            throw new VerificationException();
        }
        AppUserEntity user = appUserService.getByUsername(token.getUsername());
        if (user == null || !user.isEnabled()) {
            throw new UnknownUserException();
        }
        String dbForgotToken = user.getKeyValues().getOrDefault(FORGOTPW_KV, null);
        if (!performPasswordReset.getVerification().equals(dbForgotToken)) {
            throw new VerificationException();
        }

        appUserService.updatePassword(user.getUsername(), performPasswordReset.getPassword());

        applicationEventPublisher.publishEvent(new ResetPasswordEvent(this, user));

        return user;
    }

}
