package io.rocketbase.commons.convert;

import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class QueryAppGroupConverter implements AuthQueryConverter<QueryAppGroup> {

    public QueryAppGroup fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        return QueryAppGroup.builder()
                .ids(parseLongSet("ids", params))
                .namePath(params.getFirst("namePath"))
                .systemRefId(params.getFirst("systemRefId"))
                .name(params.getFirst("name"))
                .parentIds(parseLongSet("parentIds", params))
                .description(params.getFirst("description"))
                .keyValues(parseKeyValue("keyValues", params))
                .capabilityIds(parseLongSet("capabilityIds", params))
                .build();
    }

    public UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, QueryAppGroup query) {
        if (query != null) {
            addLongs(uriBuilder, "ids", query.getIds());
            addString(uriBuilder, "namePath", query.getNamePath());
            addString(uriBuilder, "systemRefId", query.getSystemRefId());
            addString(uriBuilder, "name", query.getName());
            addLongs(uriBuilder, "parentIds", query.getParentIds());
            addString(uriBuilder, "description", query.getDescription());
            addKeyValues(uriBuilder, "keyValues", query.getKeyValues());
            addLongs(uriBuilder, "capabilityIds", query.getCapabilityIds());
        }
        return uriBuilder;
    }
}
