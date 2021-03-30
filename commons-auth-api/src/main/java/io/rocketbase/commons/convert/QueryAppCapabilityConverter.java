package io.rocketbase.commons.convert;

import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class QueryAppCapabilityConverter implements AuthQueryConverter<QueryAppCapability> {

    public QueryAppCapability fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        return QueryAppCapability.builder()
                .ids(parseLongSet("ids", params))
                .keyPath(params.getFirst("keyPath"))
                .parentIds(parseLongSet("parentIds", params))
                .key(params.getFirst("key"))
                .description(params.getFirst("description"))
                .build();
    }

    public UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, QueryAppCapability query) {
        if (query != null) {
            addLongs(uriBuilder, "ids", query.getIds());
            addString(uriBuilder, "keyPath", query.getKeyPath());
            addLongs(uriBuilder, "parentIds", query.getParentIds());
            addString(uriBuilder, "key", query.getKey());
            addString(uriBuilder, "description", query.getDescription());
        }
        return uriBuilder;
    }
}
