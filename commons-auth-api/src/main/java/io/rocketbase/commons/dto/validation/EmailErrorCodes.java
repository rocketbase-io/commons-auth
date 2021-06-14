package io.rocketbase.commons.dto.validation;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum EmailErrorCodes {
    ALREADY_TAKEN("alreadyTaken"),
    INVALID("invalid"),
    TOO_LONG("tooLong");

    private final String value;

    EmailErrorCodes(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
