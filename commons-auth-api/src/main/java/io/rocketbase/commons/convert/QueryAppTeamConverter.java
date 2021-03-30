package io.rocketbase.commons.convert;

import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.util.QueryParamBuilder;
import io.rocketbase.commons.util.QueryParamParser;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class QueryAppTeamConverter implements AuthQueryConverter<QueryAppTeam> {

    public QueryAppTeam fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        return QueryAppTeam.builder()
                .ids(parseLongSet("ids", params))
                .name(params.getFirst("name"))
                .description(params.getFirst("description"))
                .personal(QueryParamParser.parseBoolean(params, "personal", null))
                .build();
    }

    public UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, QueryAppTeam query) {
        if (query != null) {
            addLongs(uriBuilder, "ids", query.getIds());
            addString(uriBuilder, "name", query.getName());
            addString(uriBuilder, "description", query.getDescription());
            QueryParamBuilder.appendParams(uriBuilder, "personal", query.getPersonal());
        }
        return uriBuilder;
    }
}
