package io.rocketbase.commons.convert;

import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static io.rocketbase.commons.util.QueryParamBuilder.appendParams;
import static io.rocketbase.commons.util.QueryParamParser.parseBoolean;

public class QueryAppInviteConverter implements AuthQueryConverter<QueryAppInvite> {

    public QueryAppInvite fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        return QueryAppInvite.builder()
                .invitor(params.getFirst("invitor"))
                .email(params.getFirst("email"))
                .expired(parseBoolean(params, "expired", null))
                .keyValues(parseKeyValue("keyValue", params))
                .build();
    }

    public UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, QueryAppInvite query) {
        if (query != null) {
            addString(uriBuilder, "invitor", query.getInvitor());
            addString(uriBuilder, "email", query.getEmail());
            appendParams(uriBuilder, "expired", query.getExpired());
            addKeyValues(uriBuilder, "keyValue", query.getKeyValues());
        }
        return uriBuilder;
    }
}
