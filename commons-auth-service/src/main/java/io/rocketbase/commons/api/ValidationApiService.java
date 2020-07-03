package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.ValidationConverter;
import io.rocketbase.commons.dto.validation.*;
import io.rocketbase.commons.exception.ValidationErrorCode;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class ValidationApiService implements ValidationApi {

    private final ValidationService validationService;

    @Override
    public ValidationResponse<PasswordErrorCodes> validatePassword(String password) {
        Set<ValidationErrorCode<PasswordErrorCodes>> details = validationService.getPasswordValidationDetails(null, password);
        return ValidationConverter.convert(details);
    }

    @Override
    public ValidationResponse<UsernameErrorCodes> validateUsername(String username) {
        Set<ValidationErrorCode<UsernameErrorCodes>> details = validationService.getUsernameValidationDetails(null, username);
        return ValidationConverter.convert(details);
    }

    @Override
    public ValidationResponse<EmailErrorCodes> validateEmail(String email) {
        Set<ValidationErrorCode<EmailErrorCodes>> details = validationService.getEmailValidationDetails(null, email);
        return ValidationConverter.convert(details);
    }

    @Override
    public ValidationResponse<TokenErrorCodes> validateToken(String token) {
        Set<ValidationErrorCode<TokenErrorCodes>> details = validationService.getTokenValidationDetails(token);
        return ValidationConverter.convert(details);
    }
}
