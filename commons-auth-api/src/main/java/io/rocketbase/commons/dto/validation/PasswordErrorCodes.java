package io.rocketbase.commons.dto.validation;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PasswordErrorCodes {
    TOO_SHORT("tooShort"),
    TOO_LONG("tooLong"),
    INSUFFICIENT_LOWERCASE("insufficientLowercase"),
    INSUFFICIENT_UPPERCASE("insufficientUppercase"),
    INSUFFICIENT_DIGIT("insufficientDigit"),
    INSUFFICIENT_SPECIAL("insufficientSpecial"),
    INVALID_CURRENT_PASSWORD("invalidCurrentPassword");

    private final String value;

    PasswordErrorCodes(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
