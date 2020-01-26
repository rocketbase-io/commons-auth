package io.rocketbase.commons.exception;

import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@RequiredArgsConstructor
public class PasswordValidationException extends RuntimeException {

    private final Set<ValidationErrorCode<PasswordErrorCodes>> errors;

}
