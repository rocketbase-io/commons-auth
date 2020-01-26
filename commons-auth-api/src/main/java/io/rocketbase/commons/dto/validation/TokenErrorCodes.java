package io.rocketbase.commons.dto.validation;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum TokenErrorCodes {
    EXPIRED("expired");

    @Getter
    @JsonValue
    private String value;

    TokenErrorCodes(String value) {
        this.value = value;
    }
}
