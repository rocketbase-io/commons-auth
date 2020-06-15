package io.rocketbase.commons.convert;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static io.rocketbase.commons.util.QueryParamBuilder.appendParams;
import static io.rocketbase.commons.util.QueryParamParser.parseBoolean;

public class QueryAppUserConverter implements AuthQueryConverter<QueryAppUser> {

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
                .keyValues(parseKeyValue("keyValue", params))
                .build();
    }

    public UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, QueryAppUser query) {
        if (query != null) {
            addString(uriBuilder, "username", query.getUsername());
            addString(uriBuilder, "firstName", query.getFirstName());
            addString(uriBuilder, "lastName", query.getLastName());
            addString(uriBuilder, "email", query.getEmail());
            addString(uriBuilder, "freetext", query.getFreetext());
            appendParams(uriBuilder, "enabled", query.getEnabled());
            addString(uriBuilder, "hasRole", query.getHasRole());
            addKeyValues(uriBuilder, "keyValue", query.getKeyValues());
        }
        return uriBuilder;
    }
}
