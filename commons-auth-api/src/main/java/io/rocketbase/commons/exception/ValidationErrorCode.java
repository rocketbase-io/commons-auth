package io.rocketbase.commons.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode(of = "code")
@RequiredArgsConstructor
public class ValidationErrorCode<T extends Enum<T>> {

    private final T code;
    private final String message;

    public ValidationErrorCode(T code) {
        this.code = code;
        message = null;
    }
}
