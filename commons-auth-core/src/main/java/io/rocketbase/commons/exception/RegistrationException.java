package io.rocketbase.commons.exception;

import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@RequiredArgsConstructor
public class RegistrationException extends RuntimeException {

    private final Set<UsernameErrorCodes> usernameErrors;
    private final Set<PasswordErrorCodes> passwordErrors;
    private final Set<EmailErrorCodes> emailErrors;

}
