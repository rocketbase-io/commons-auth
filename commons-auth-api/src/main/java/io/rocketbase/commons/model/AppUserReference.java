package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@JsonDeserialize(as = SimpleAppUserReference.class)
public interface AppUserReference extends Serializable {

    String getId();

    String getUsername();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getAvatar();

    /**
     * combines first + last name
     */
    @JsonIgnore
    default String getFullName() {
        boolean emptyFirstName = StringUtils.isEmpty(getFirstName());
        boolean emptyLastName = StringUtils.isEmpty(getLastName());
        if (emptyFirstName && emptyLastName) {
            return null;
        } else if (!emptyFirstName && !emptyLastName) {
            return String.format("%s %s", getFirstName(), getLastName());
        } else if (!emptyFirstName) {
            return getFirstName();
        } else {
            return getLastName();
        }
    }

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
