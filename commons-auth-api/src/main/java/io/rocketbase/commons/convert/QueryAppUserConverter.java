package io.rocketbase.commons.convert;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.util.QueryParamBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

import static io.rocketbase.commons.util.QueryParamParser.parseBoolean;

public class QueryAppUserConverter implements AuthQueryConverter<QueryAppUser> {

    public QueryAppUser fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }

        return QueryAppUser.builder()
                .username(params.getFirst("username"))
                .firstName(params.getFirst("firstName"))
                .lastName(params.getFirst("lastName"))
                .email(params.getFirst("email"))
                .freetext(params.getFirst("freetext"))
                .enabled(parseBoolean(params, "enabled", null))
                .capabilityIds(params.containsKey("capabilityId") ?
                        params.get("capabilityId")
                                .stream()
                                .filter(v -> v.matches("[0-9]]+"))
                                .map(v -> Long.valueOf(v))
                                .collect(Collectors.toSet()) : null)
                .groupIds(params.containsKey("groupId") ?
                        params.get("groupId")
                                .stream()
                                .filter(v -> v.matches("[0-9]]+"))
                                .map(v -> Long.valueOf(v))
                                .collect(Collectors.toSet()) : null)
                .keyValues(parseKeyValue("keyValue", params))
                .build();
    }

    public UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, QueryAppUser query) {
        if (query != null) {
            QueryParamBuilder.appendParams(uriBuilder, "username", query.getUsername());
            QueryParamBuilder.appendParams(uriBuilder, "firstName", query.getFirstName());
            QueryParamBuilder.appendParams(uriBuilder, "lastName", query.getLastName());
            QueryParamBuilder.appendParams(uriBuilder, "email", query.getEmail());
            QueryParamBuilder.appendParams(uriBuilder, "freetext", query.getFreetext());
            QueryParamBuilder.appendParams(uriBuilder, "enabled", query.getEnabled());
            QueryParamBuilder.appendParamNumbers(uriBuilder, "capabilityId", query.getCapabilityIds());
            QueryParamBuilder.appendParamNumbers(uriBuilder, "groupId", query.getGroupIds());
            QueryParamBuilder.appendParams(uriBuilder, "keyValue", query.getKeyValues());
        }
        return uriBuilder;
    }
}
