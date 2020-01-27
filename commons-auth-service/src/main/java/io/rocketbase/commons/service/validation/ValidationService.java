package io.rocketbase.commons.service.validation;

import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.exception.PasswordValidationException;
import io.rocketbase.commons.exception.UsernameValidationException;
import io.rocketbase.commons.exception.ValidationErrorCode;

import java.util.Set;

public interface ValidationService {

    int EMAIL_MAX_LENGTH = 255;

    boolean isPasswordValid(String password);

    void passwordIsValid(String password) throws PasswordValidationException;

    Set<ValidationErrorCode<PasswordErrorCodes>> getPasswordValidationDetails(String password);

    boolean isUsernameValid(String username);

    void usernameIsValid(String username) throws UsernameValidationException;

    Set<ValidationErrorCode<UsernameErrorCodes>> getUsernameValidationDetails(String username);

    boolean isEmailValid(String email);

    Set<ValidationErrorCode<EmailErrorCodes>> getEmailValidationDetails(String email);

    void emailIsValid(String email);

    boolean validateRegistration(String username, String password, String email);

}
