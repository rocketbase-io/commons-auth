package io.rocketbase.commons.dto.validation;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenErrorCodes {
    EXPIRED("expired"),
    INVALID("invalid");

    private final String value;

    TokenErrorCodes(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
