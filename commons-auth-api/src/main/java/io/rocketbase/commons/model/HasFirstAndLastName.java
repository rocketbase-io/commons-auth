package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;

public interface HasFirstAndLastName {

    @Nullable
    String getFirstName();

    @Nullable
    String getLastName();

    /**
     * combines first + last name<br>
     * will return null in case firstName & lastName is empty
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
}
