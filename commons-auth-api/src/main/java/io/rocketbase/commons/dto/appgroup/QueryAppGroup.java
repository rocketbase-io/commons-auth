package io.rocketbase.commons.dto.appgroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * query object to find {@link AppGroupRead}
 * string properties mean search like ignore cases
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAppGroup implements Serializable {

    @Nullable
    private Set<Long> ids;

    @Nullable
    private String treePath;

    @Nullable
    private String systemRefId;

    @Nullable
    private String name;

    @Nullable
    private Set<Long> parentIds;

    @Nullable
    private String description;

    /**
     * search for given key and value with exact match
     */
    @Nullable
    private Map<String, String> keyValues;

    /**
     * used needs to have capability ids
     */
    @Nullable
    private Set<Long> capabilityIds;

    /**
     * used needs to have capability ids
     */
    @Nullable
    private Set<String> capabilityKeyPaths;


}
