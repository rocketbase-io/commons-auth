package io.rocketbase.commons.exception;

import java.util.Set;
import java.util.stream.Collectors;

public interface BaseValidationException<T extends Enum<T>> {
    Set<ValidationErrorCode<T>> getErrors();

    /**
     * joins all error message to one string<br>
     * returns empty string when no errors exist
     */
    default String getErrorsMessage() {
        if (getErrors() != null) {
            return getErrors().stream().map(e -> e.getMessage()).collect(Collectors.joining("; "));
        }
        return "";
    }
}
