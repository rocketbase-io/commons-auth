package io.rocketbase.commons.convert;

import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public interface KeyValueQueryParser {

    default Map<String, String> parseKeyValue(MultiValueMap<String, String> params, String key) {
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
}
