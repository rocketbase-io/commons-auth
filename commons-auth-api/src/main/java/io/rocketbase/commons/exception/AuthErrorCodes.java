package io.rocketbase.commons.exception;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum AuthErrorCodes {
    REGISTRATION(1010, "registration"),
    VERIFICATION_INVALID(1011, "verificationInvalid"),
    UNKNOWN_USER(1012, "unknownUser"),
    VALIDATION(1013, "validation");

    @Getter
    private final int status;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
