package io.rocketbase.commons.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(as = SimpleAppUserToken.class)
public interface AppUserToken extends AppUserReference, HasKeyValue {

    List<String> getRoles();

    /**
     * checks if user has role with name (ignore cases)
     *
     * @param role name of role to search
     * @return true when exists
     */
    default boolean hasRole(String role) {
        if (getRoles() != null && role != null) {
            for (String r : getRoles()) {
                if (role.equalsIgnoreCase(r)) {
                    return true;
                }
            }
        }
        return false;
    }

}
