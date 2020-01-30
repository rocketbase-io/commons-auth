package io.rocketbase.commons.exception;

import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@RequiredArgsConstructor
public class UsernameValidationException extends RuntimeException {

    private final Set<ValidationErrorCode<UsernameErrorCodes>> errors;

}
