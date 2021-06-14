package io.rocketbase.commons.dto.validation;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UsernameErrorCodes {
    ALREADY_TAKEN("alreadyTaken"),
    TOO_SHORT("tooShort"),
    TOO_LONG("tooLong"),
    NOT_ALLOWED_CHAR("notAllowedChar");

    private final String value;

    UsernameErrorCodes(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
