package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.validation.*;

public interface ValidationApi {
    ValidationResponse<PasswordErrorCodes> validatePassword(String password);

    ValidationResponse<UsernameErrorCodes> validateUsername(String username);

    ValidationResponse<EmailErrorCodes> validateEmail(String email);

    ValidationResponse<TokenErrorCodes> validateToken(String token);
}
