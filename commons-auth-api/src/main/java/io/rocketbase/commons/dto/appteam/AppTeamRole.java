package io.rocketbase.commons.dto.appteam;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum AppTeamRole {

    OWNER("owner"),
    MEMBER("member");

    @Getter
    @JsonValue
    private final String value;

    AppTeamRole(String value) {
        this.value = value;
    }
}
