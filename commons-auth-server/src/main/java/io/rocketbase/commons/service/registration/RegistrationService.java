package io.rocketbase.commons.service.registration;

import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.FeedbackActionService;

public interface RegistrationService extends FeedbackActionService {

    AppUser register(RegistrationRequest registration, String baseUrl);

    AppUser verifyRegistration(String verification);
}
