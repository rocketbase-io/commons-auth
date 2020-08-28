package io.rocketbase.commons.model;

import org.springframework.util.Assert;

public interface EntityWithKeyValue<T> extends HasKeyValue {

    /**
     * @param key   max length of 50 characters<br>
     *              key with _ as prefix will not get displayed in REST_API
     * @param value max length of 4000 characters
     * @return itself for fluent api
     */
    default T addKeyValue(String key, String value) {
        checkKeyValue(key, value);
        getKeyValues().put(key, value);
        return (T) this;
    }

    default void removeKeyValue(String key) {
        getKeyValues().remove(key);
    }

    default void checkKeyValue(String key, String value) {
        Assert.hasLength(key, "Key must not be empty");
        Assert.state(key.length() <= 50, "Key is too long - at least 50 chars");
        Assert.state(key.toLowerCase().matches("[a-zA-Z0-9_\\-\\.]+"), "Allowed key chars are a-Z, 0-9 and _-.");
        Assert.hasLength(value, "Value must not be empty");
        Assert.state(value.length() <= 4000, "Value is too long - at least 4000 chars");
    }
}
