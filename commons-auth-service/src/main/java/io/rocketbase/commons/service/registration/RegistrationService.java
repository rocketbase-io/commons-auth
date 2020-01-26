package io.rocketbase.commons.service.registration;

import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.FeedbackActionService;

public interface RegistrationService extends FeedbackActionService {

    AppUserEntity register(RegistrationRequest registration, String baseUrl);

    AppUserEntity verifyRegistration(String verification);
}
