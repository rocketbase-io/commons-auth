package io.rocketbase.commons.exception;

import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@RequiredArgsConstructor
public class EmailValidationException extends RuntimeException implements BaseValidationException {

    private final Set<ValidationErrorCode<EmailErrorCodes>> errors;

}
