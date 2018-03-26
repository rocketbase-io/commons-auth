package io.rocketbase.commons.exception;

import lombok.Getter;

public enum AuthErrorCodes {
    REGISTRATION_ALREADY_IN_USE(1010),
    VERIFICATION_INVALID(1011);

    @Getter
    private int status;

    AuthErrorCodes(int status) {
        this.status = status;
    }
}
