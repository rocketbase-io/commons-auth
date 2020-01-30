package io.rocketbase.commons.exception;

import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@ToString
@RequiredArgsConstructor
public class RegistrationException extends RuntimeException implements BaseValidationException {

    private final Set<ValidationErrorCode<UsernameErrorCodes>> usernameErrors;
    private final Set<ValidationErrorCode<PasswordErrorCodes>> passwordErrors;
    private final Set<ValidationErrorCode<EmailErrorCodes>> emailErrors;

    @Override
    public Set<ValidationErrorCode> getErrors() {
        Set<ValidationErrorCode> result = new LinkedHashSet<>();
        if (hasUsernameErrors()) {
            result.addAll(usernameErrors);
        }
        if (hasPasswordErrors()) {
            result.addAll(passwordErrors);
        }
        if (hasEmailErrors()) {
            result.addAll(emailErrors);
        }
        return null;
    }

    public boolean hasUsernameErrors() {
        return usernameErrors != null && !usernameErrors.isEmpty();
    }

    public boolean hasPasswordErrors() {
        return passwordErrors != null && !passwordErrors.isEmpty();
    }

    public boolean hasEmailErrors() {
        return emailErrors != null && !emailErrors.isEmpty();
    }
}
