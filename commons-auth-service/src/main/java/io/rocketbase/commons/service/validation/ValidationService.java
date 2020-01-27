package io.rocketbase.commons.service.validation;

import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.exception.*;

import java.util.Set;

public interface ValidationService {

    int EMAIL_MAX_LENGTH = 255;

    default boolean isPasswordValid(String password) {
        return getPasswordValidationDetails(password).isEmpty();
    }

    /**
     * validate and throw error in case of errors
     */
    void passwordIsValid(String password) throws PasswordValidationException;

    Set<ValidationErrorCode<PasswordErrorCodes>> getPasswordValidationDetails(String password);

    default boolean isUsernameValid(String username) {
        return getUsernameValidationDetails(username).isEmpty();
    }

    /**
     * validate and throw error in case of errors
     */
    void usernameIsValid(String username) throws UsernameValidationException;

    Set<ValidationErrorCode<UsernameErrorCodes>> getUsernameValidationDetails(String username);

    default boolean isEmailValid(String email) {
        return getEmailValidationDetails(email).isEmpty();
    }

    Set<ValidationErrorCode<EmailErrorCodes>> getEmailValidationDetails(String email);

    /**
     * validate and throw error in case of errors
     */
    void emailIsValid(String email) throws EmailValidationException;

    void registrationIsValid(String username, String password, String email) throws RegistrationException;

}
