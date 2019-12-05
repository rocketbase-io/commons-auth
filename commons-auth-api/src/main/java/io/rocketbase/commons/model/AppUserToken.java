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

    default boolean hasKeyValue(String key) {
        return getKeyValues() != null && key != null && getKeyValues().containsKey(key.toLowerCase());
    }

    default String getKeyValue(String key) {
        return getKeyValues() != null && key != null ? getKeyValues().getOrDefault(key.toLowerCase(), null) : null;
    }

}
