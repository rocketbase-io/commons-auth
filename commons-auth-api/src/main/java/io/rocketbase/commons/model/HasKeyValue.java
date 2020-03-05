package io.rocketbase.commons.model;

import javax.annotation.Nullable;
import java.util.Map;

public interface HasKeyValue {

    /**
     * @return an immutable map so that changes should only be done by add/remove KeyValue
     */
    @Nullable
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

}
