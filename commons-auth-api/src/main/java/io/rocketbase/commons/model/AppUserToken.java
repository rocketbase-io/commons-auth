package io.rocketbase.commons.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;

@JsonDeserialize(as = SimpleAppUserToken.class)
public interface AppUserToken extends AppUserReference {

    List<String> getRoles();

    /**
     * @return an immutable map so that changes should only be done by add/remove KeyValue
     */
    Map<String, String> getKeyValues();

    /**
     * checks if user has key (ignore cases)
     *
     * @param key name of key
     * @return true when exists
     */
    default boolean hasKeyValue(String key) {
        return getKeyValues() != null && key != null && getKeyValues().containsKey(key.toLowerCase());
    }

    /**
     * search for value of given key
     *
     * @param key name of key (ignore cases)
     * @return value or null when not found
     */
    default String getKeyValue(String key) {
        return getKeyValues() != null && key != null ? getKeyValues().getOrDefault(key.toLowerCase(), null) : null;
    }

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
