package io.rocketbase.commons.exception;

import com.google.common.base.Joiner;

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
            return Joiner.on("; ").skipNulls().join(getErrors().stream().map(e -> e.getMessage()).collect(Collectors.toList()));
        }
        return "";
    }
}
