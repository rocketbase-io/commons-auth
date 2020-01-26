package io.rocketbase.commons.dto.validation;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum UsernameErrorCodes {
    ALREADY_TAKEN("alreadyTaken"),
    TOO_SHORT("tooShort"),
    TOO_LONG("tooLong"),
    NOT_ALLOWED_CHAR("notAllowedChar");

    @Getter
    @JsonValue
    private String value;

    UsernameErrorCodes(String value) {
        this.value = value;
    }
}
