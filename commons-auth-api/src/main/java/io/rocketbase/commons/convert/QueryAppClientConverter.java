package io.rocketbase.commons.convert;

import io.rocketbase.commons.dto.appclient.QueryAppClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class QueryAppClientConverter implements AuthQueryConverter<QueryAppClient> {

    public QueryAppClient fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        return QueryAppClient.builder()
                .ids(parseLongSet("ids", params))
                .name(params.getFirst("name"))
                .systemRefId(params.getFirst("systemRefId"))
                .capabilityIds(parseLongSet("capabilityIds", params))
                .redirectUrl(params.getFirst("redirectUrl"))
                .description(params.getFirst("description"))
                .build();
    }

    public UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, QueryAppClient query) {
        if (query != null) {
            addLongs(uriBuilder, "ids", query.getIds());
            addString(uriBuilder, "name", query.getName());
            addString(uriBuilder, "systemRefId", query.getSystemRefId());
            addLongs(uriBuilder, "capabilityIds", query.getCapabilityIds());
            addString(uriBuilder, "redirectUrl", query.getRedirectUrl());
            addString(uriBuilder, "description", query.getDescription());
        }
        return uriBuilder;
    }
}
