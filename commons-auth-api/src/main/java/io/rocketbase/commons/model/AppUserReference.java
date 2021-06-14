package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * short representation of an appuser
 */
@JsonDeserialize(as = SimpleAppUserReference.class)
@Schema(description = "short representation of an appuser")
public interface AppUserReference extends HasFirstAndLastName, Serializable {

    String getId();

    String getUsername();

    String getEmail();

    @Nullable
    String getAvatar();

    /**
     * fullname fallback if null use username
     */
    @JsonIgnore
    default String getDisplayName() {
        String fullName = getFullName();
        if (fullName == null) {
            return getUsername();
        }
        return fullName;
    }
}
