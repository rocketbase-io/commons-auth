package io.rocketbase.commons.service.validation;

import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.TokenErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.exception.*;

import java.util.Set;

public interface ValidationService {

    int EMAIL_MAX_LENGTH = 255;

    default boolean isPasswordValid(String field, String password) {
        return getPasswordValidationDetails(field, password).isEmpty();
    }

    default boolean isPasswordValid(String password) {
        return isPasswordValid("password", password);
    }

    /**
     * validate and throw error in case of errors
     */
    void passwordIsValid(String field, String password) throws PasswordValidationException;

    Set<ValidationErrorCode<PasswordErrorCodes>> getPasswordValidationDetails(String field, String password);

    default Set<ValidationErrorCode<PasswordErrorCodes>> getPasswordValidationDetails(String password) {
        return getPasswordValidationDetails("password", password);
    }

    default boolean isUsernameValid(String field, String username) {
        return getUsernameValidationDetails(field, username).isEmpty();
    }

    default boolean isUsernameValid(String username) {
        return isUsernameValid("username", username);
    }

    /**
     * validate and throw error in case of errors
     */
    void usernameIsValid(String field, String username) throws UsernameValidationException;

    Set<ValidationErrorCode<UsernameErrorCodes>> getUsernameValidationDetails(String field, String username);

    default Set<ValidationErrorCode<UsernameErrorCodes>> getUsernameValidationDetails(String username) {
        return getUsernameValidationDetails("username", username);
    }

    default boolean isEmailValid(String field, String email) {
        return getEmailValidationDetails(field, email).isEmpty();
    }

    default boolean isEmailValid(String email) {
        return isEmailValid("email", email);
    }

    Set<ValidationErrorCode<EmailErrorCodes>> getEmailValidationDetails(String field, String email);

    default Set<ValidationErrorCode<EmailErrorCodes>> getEmailValidationDetails(String email) {
        return getEmailValidationDetails("email", email);
    }

    /**
     * validate and throw error in case of errors
     */
    void emailIsValid(String field, String email) throws EmailValidationException;

    void registrationIsValid(String username, String password, String email) throws RegistrationException;

    Set<ValidationErrorCode<TokenErrorCodes>> getTokenValidationDetails(String token);

}
