package io.rocketbase.commons.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * AppUserReference + role information + keyValues
 */
@JsonDeserialize(as = SimpleAppUserToken.class)
@Schema(description = "AppUserReference + role information + keyValues")
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
