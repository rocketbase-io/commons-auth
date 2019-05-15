package io.rocketbase.commons.service.validation;

import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.exception.PasswordValidationException;
import io.rocketbase.commons.exception.UsernameValidationException;

import java.util.Set;

public interface ValidationService {
    boolean isPasswordValid(String password);

    void passwordIsValid(String password) throws PasswordValidationException;

    Set<PasswordErrorCodes> getPasswordValidationDetails(String password);

    boolean isUsernameValid(String username);

    void usernameIsValid(String username) throws UsernameValidationException;

    Set<UsernameErrorCodes> getUsernameValidationDetails(String username);

    boolean isEmailValid(String email);

    Set<EmailErrorCodes> getEmailValidationDetails(String email);

    void emailIsValid(String email);

    boolean validateRegistration(String username, String password, String email);
}
