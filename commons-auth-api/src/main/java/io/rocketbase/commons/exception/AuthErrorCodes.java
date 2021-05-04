package io.rocketbase.commons.exception;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum AuthErrorCodes {
    REGISTRATION("REGISTRATION", 1010),
    VERIFICATION_INVALID("VERIFICATION_INVALID", 1011),
    UNKNOWN_USER("UNKNOWN_USER", 1012),
    VALIDATION("VALIDATION", 1013),
    JWT("JWT", 1014),
    EMAIL_DELIVERY("EMAIL_DELIVERY", 1015);

    @Getter
    @JsonValue
    private final String value;

    @Getter
    private final int status;

    AuthErrorCodes(String value, int status) {
        this.value = value;
        this.status = status;
    }
}
