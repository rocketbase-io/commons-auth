package io.rocketbase.commons.service.registration;

import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.service.FeedbackActionService;

public interface RegistrationService extends FeedbackActionService {

    ExpirationInfo<AppUserEntity> register(RegistrationRequest registration, String baseUrl) throws RegistrationException;

    AppUserToken verifyRegistration(String verification) throws VerificationException;
}
