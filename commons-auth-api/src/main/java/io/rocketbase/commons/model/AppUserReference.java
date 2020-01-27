package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;

@JsonDeserialize(as = SimpleAppUserReference.class)
public interface AppUserReference extends HasFirstAndLastName, Serializable {

    String getId();

    String getUsername();

    String getEmail();

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
