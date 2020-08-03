package io.rocketbase.commons.service.change;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.authentication.EmailChangeRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.FeedbackActionService;

public interface ChangeAppUserWithConfirmService extends FeedbackActionService {

    ExpirationInfo<AppUserEntity> handleEmailChangeRequest(String userId, EmailChangeRequest changeRequest, String baseUrl) throws EmailValidationException;

    AppUserEntity confirmEmailChange(String verification) throws VerificationException;
}
