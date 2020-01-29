package io.rocketbase.commons.convert;

import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import org.springframework.util.MultiValueMap;

import static io.rocketbase.commons.util.QueryParamParser.parseBoolean;

public class QueryAppInviteConverter implements KeyValueQueryParser {

    public QueryAppInvite fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        return QueryAppInvite.builder()
                .invitor(params.containsKey("invitor") ? params.getFirst("invitor") : null)
                .email(params.containsKey("email") ? params.getFirst("email") : null)
                .expired(parseBoolean(params, "expired", null))
                .keyValues(parseKeyValue(params, "keyValue"))
                .build();
    }
}
