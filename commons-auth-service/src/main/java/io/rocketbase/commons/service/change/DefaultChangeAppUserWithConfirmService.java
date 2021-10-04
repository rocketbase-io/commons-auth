package io.rocketbase.commons.service.change;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.authentication.EmailChangeRequest;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.SimpleTokenService;
import io.rocketbase.commons.service.email.AuthEmailService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class DefaultChangeAppUserWithConfirmService implements ChangeAppUserWithConfirmService {

    public static String CHANGEMAIL_TOKEN = "_changemail";
    public static String CHANGEMAIL_VALUE = "_newmail";
    @Getter
    final AuthProperties authProperties;

    @Resource
    private AppUserService appUserService;

    @Resource
    private ValidationService validationService;

    @Resource
    private AuthEmailService emailService;

    @Override
    public ExpirationInfo<AppUserEntity> handleEmailChangeRequest(String userId, EmailChangeRequest changeRequest, String baseUrl) {
        AppUserEntity entity = appUserService.findById(userId).orElseThrow(NotFoundException::new);
        validationService.emailIsValid("newEmail", changeRequest.getNewEmail());

        if (authProperties.isVerifyEmail()) {
            try {
                Instant expires = Instant.now().plus(authProperties.getChangeEmailExpiration());
                String token = SimpleTokenService.generateToken(entity.getUsername(), authProperties.getChangeEmailExpiration());
                appUserService.updateKeyValues(entity.getUsername(), ImmutableMap.of(CHANGEMAIL_TOKEN, token, CHANGEMAIL_VALUE, changeRequest.getNewEmail()));

                emailService.sentChangeEmailAddressEmail(entity, changeRequest.getNewEmail(), buildActionUrl(baseUrl, ActionType.CHANGE_EMAIL, token, authProperties.getChangeEmailUrl()));
                return ExpirationInfo.<AppUserEntity>builder()
                        .expires(expires)
                        .detail(entity)
                        .build();
            } catch (Exception e) {
                log.error("couldn't sent email. please check your configuration. {}", e.getMessage());
                appUserService.updateKeyValues(entity.getUsername(), ImmutableMap.of(CHANGEMAIL_TOKEN, null, CHANGEMAIL_VALUE, null));
                throw e;
            }
        } else {
            entity = appUserService.changeEmail(entity.getId(), changeRequest.getNewEmail());
            return ExpirationInfo.<AppUserEntity>builder()
                    .expires(null)
                    .detail(entity)
                    .build();
        }
    }

    @Override
    public AppUserEntity confirmEmailChange(String verification) throws VerificationException {
        SimpleTokenService.Token token = SimpleTokenService.parseToken(verification);
        if (!token.isValid()) {
            throw new VerificationException("verification");
        }
        AppUserEntity entity = appUserService.getByUsername(token.getUsername());
        String dbValue = entity.getKeyValues().getOrDefault(CHANGEMAIL_TOKEN, null);
        if (!verification.equals(dbValue)) {
            throw new VerificationException("verification");
        }

        return appUserService.changeEmail(entity.getId(), entity.getKeyValues().getOrDefault(CHANGEMAIL_VALUE, null));
    }

}
