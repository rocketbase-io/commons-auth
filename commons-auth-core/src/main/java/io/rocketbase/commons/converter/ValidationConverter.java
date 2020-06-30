package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.validation.ValidationResponse;
import io.rocketbase.commons.exception.ValidationErrorCode;

import java.util.HashMap;
import java.util.Set;

public class ValidationConverter {

    public static <T extends Enum<T>> ValidationResponse<T> convert(Set<ValidationErrorCode<T>> errors) {
        ValidationResponse<T> response = new ValidationResponse<>(errors != null && errors.isEmpty(), new HashMap<>());
        if (errors != null) {
            for (ValidationErrorCode<T> c : errors) {
                response.getErrorCodes().put(c.getCode(), c.getMessage());
            }
        }
        return response;
    }
}
