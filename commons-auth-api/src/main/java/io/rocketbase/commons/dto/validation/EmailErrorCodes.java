package io.rocketbase.commons.dto.validation;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum EmailErrorCodes {
    ALREADY_TAKEN("alreadyTaken"),
    INVALID("invalid"),
    TOO_LONG("tooLong");

    @Getter
    @JsonValue
    private final String value;

    EmailErrorCodes(String value) {
        this.value = value;
    }
}
