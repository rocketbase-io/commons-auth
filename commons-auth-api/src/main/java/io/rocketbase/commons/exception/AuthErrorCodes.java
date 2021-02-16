package io.rocketbase.commons.exception;

import lombok.Getter;

public enum AuthErrorCodes {
    REGISTRATION(1010),
    VERIFICATION_INVALID(1011),
    UNKNOWN_USER(1012),
    VALIDATION(1013),
    JWT(1014),
    EMAIL_DELIVERY(1015);

    @Getter
    private final int status;

    AuthErrorCodes(int status) {
        this.status = status;
    }
}
