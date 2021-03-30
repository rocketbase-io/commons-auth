package io.rocketbase.commons.convert;

import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

public interface AuthQueryConverter<T> {

    default Map<String, String> parseKeyValue(String key, MultiValueMap<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if (params != null && params.containsKey(key)) {
            for (String kv : params.get(key)) {
                String[] split = StringUtils.split(kv, ";");
                if (split != null) {
                    result.put(split[0], split[1]);
                }
            }
        }
        return result;
    }

    default Set<Long> parseLongSet(String key, MultiValueMap<String, String> params) {
        Set<Long> result = new HashSet<>();
        if (params != null && params.containsKey(key)) {
            for (String v : params.get(key)) {
                if (v.matches("[0-9]+")) {
                    result.add(Long.parseLong(v));
                }
            }
        }
        return result.isEmpty() ? null : result;
    }

    default void addKeyValues(UriComponentsBuilder uriBuilder, String key, Map<String, String> keyValues) {
        if (uriBuilder != null && key != null && keyValues != null && !keyValues.isEmpty()) {
            for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                uriBuilder.queryParam(key, String.format("%s;%s", entry.getKey(), entry.getValue()));
            }
        }
    }

    default void addString(UriComponentsBuilder uriBuilder, String key, String value) {
        if (uriBuilder != null && value != null && key != null) {
            uriBuilder.queryParam(key, value);
        }
    }

    default void addString(UriComponentsBuilder uriBuilder, String key, Collection<String> value) {
        if (uriBuilder != null && value != null && !value.isEmpty() && key != null) {
            uriBuilder.queryParam(key, value);
        }
    }
    default void addLongs(UriComponentsBuilder uriBuilder, String key, Collection<Long> value) {
        if (uriBuilder != null && value != null && !value.isEmpty() && key != null) {
            uriBuilder.queryParam(key, value);
        }
    }

    T fromParams(MultiValueMap<String, String> params);

    UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, T query);
}
