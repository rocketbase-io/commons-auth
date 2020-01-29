package io.rocketbase.commons.convert;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import org.springframework.util.MultiValueMap;

import static io.rocketbase.commons.util.QueryParamParser.parseBoolean;

public class QueryAppUserConverter implements KeyValueQueryParser {

    public QueryAppUser fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        return QueryAppUser.builder()
                .username(params.containsKey("username") ? params.getFirst("username") : null)
                .firstName(params.containsKey("firstName") ? params.getFirst("firstName") : null)
                .lastName(params.containsKey("lastName") ? params.getFirst("lastName") : null)
                .email(params.containsKey("email") ? params.getFirst("email") : null)
                .freetext(params.containsKey("freetext") ? params.getFirst("freetext") : null)
                .enabled(parseBoolean(params, "enabled", null))
                .hasRole(params.containsKey("hasRole") ? params.getFirst("hasRole") : null)
                .keyValues(parseKeyValue(params, "keyValue"))
                .build();
    }
}
