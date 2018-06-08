package io.rocketbase.commons.service;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.event.ForgotPasswordEvent;
import io.rocketbase.commons.event.ResetPasswordEvent;
import io.rocketbase.commons.exception.UnknownUserException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.Optional;

import static io.rocketbase.commons.service.AppUserService.FORGOTPW_KV;

@RequiredArgsConstructor
public class AppUserForgotPasswordService {

    final AuthProperties authProperties;

    @Resource
    private AppUserService appUserService;

    @Resource
    private EmailService emailService;

    @Resource
    private TokenizerService tokenizerService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AppUser requestPasswordReset(ForgotPasswordRequest forgotPassword, String baseUrl) {
        Optional<AppUser> optional = appUserService.findByEmail(forgotPassword.getEmail().toLowerCase());
        if (!optional.isPresent() || !optional.get().isEnabled()) {
            throw new UnknownUserException();
        }
        String token = tokenizerService.generateToken(optional.get().getUsername(), null, authProperties.getPasswordResetExpiration());
        appUserService.updateKeyValues(optional.get().getUsername(), ImmutableMap.of(FORGOTPW_KV, token));

        emailService.sentForgotPasswordEmail(optional.get(), baseUrl, token);

        applicationEventPublisher.publishEvent(new ForgotPasswordEvent(this, optional.get()));

        return optional.get();
    }

    public AppUser resetPassword(PerformPasswordResetRequest performPasswordReset) {
        TokenizerService.Token token = tokenizerService.parseToken(performPasswordReset.getVerification());
        if (!token.isValid()) {
            throw new VerificationException();
        }
        AppUser user = appUserService.getByUsername(token.getUsername());
        if (user == null || !user.isEnabled()) {
            throw new UnknownUserException();
        }
        String dbForgotToken = user.getKeyValues().getOrDefault(FORGOTPW_KV, null);
        if (!token.equals(dbForgotToken)) {
            throw new VerificationException();
        }

        appUserService.updatePassword(user.getUsername(), performPasswordReset.getPassword());

        applicationEventPublisher.publishEvent(new ResetPasswordEvent(this, user));

        return user;
    }

}
