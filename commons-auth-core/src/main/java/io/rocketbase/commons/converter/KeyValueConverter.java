package io.rocketbase.commons.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class KeyValueConverter {

    public static Map<String, String> filterInvisibleKeys(Map<String, String> keyValues) {
        return filterKeyValues(keyValues, e -> !e.getKey().startsWith("_"));
    }

    public static Map<String, String> filterInvisibleAndJwtIgnoredKeys(Map<String, String> keyValues) {
        return filterKeyValues(keyValues, e -> !e.getKey().startsWith("_") && !e.getKey().startsWith("#"));
    }

    private static Map<String, String> filterKeyValues(Map<String, String> keyValues, Predicate<Map.Entry<String, String>> predicate) {
        if (keyValues == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        keyValues.entrySet().stream()
                .filter(predicate)
                .forEach(e -> map.put(e.getKey(), e.getValue()));
        return map;
    }
}
