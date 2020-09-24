package io.rocketbase.commons.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public interface HasKeyValue {

    /**
     * @return an immutable map so that changes should only be done by add/remove KeyValue
     */
    @Nullable
    Map<String, String> getKeyValues();

    /**
     * checks if user has key
     *
     * @param key name of key
     * @return true when exists
     */
    default boolean hasKeyValue(String key) {
        return getKeyValues() != null && key != null && getKeyValues().containsKey(key);
    }

    /**
     * search for value of given key
     *
     * @param key name of key
     * @return value or null when not found
     */
    default String getKeyValue(String key) {
        return getKeyValues() != null && key != null ? getKeyValues().getOrDefault(key, null) : null;
    }

    /**
     * search for value of given key and parse json-string
     *
     * @param key       name of key
     * @param reference used by objectMapper
     * @param fallback  when key not found or not readable
     * @return value or fallback
     */
    default <T> T getKeyValue(String key, TypeReference<T> reference, T fallback) {
        String value = getKeyValue(key);
        if (value == null) {
            return fallback;
        }
        try {
            return new ObjectMapper().readValue(value, reference);
        } catch (JsonProcessingException e) {
            return fallback;
        }
    }

    /**
     * search for value of given key and parse json-string
     *
     * @param key      name of key
     * @param fallback when key not found or not readable
     * @return value or fallback
     */
    default Boolean getKeyValueBoolean(String key, Boolean fallback) {
        return getKeyValue(key, new TypeReference<Boolean>() {
        }, fallback);
    }

    /**
     * search for value of given key and parse json-string
     *
     * @param key      name of key
     * @param fallback when key not found or not readable
     * @return value or fallback
     */
    default Long getKeyValueLong(String key, Long fallback) {
        return getKeyValue(key, new TypeReference<Long>() {
        }, fallback);
    }

    /**
     * search for value of given key and parse json-string
     *
     * @param key      name of key
     * @param fallback when key not found or not readable
     * @return value or fallback
     */
    default Collection<String> getKeyValueCollection(String key, Collection<String> fallback) {
        return getKeyValue(key, new TypeReference<Collection<String>>() {
        }, fallback);
    }

}
