package io.rocketbase.commons.dto.appcapability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Set;

/**
 * query object to find {@link AppCapabilityRead}
 * string properties mean search like ignore cases
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAppCapability implements Serializable {

    @Nullable
    private Set<Long> ids;

    @Nullable
    private String keyPath;

    @Nullable
    private Set<Long> parentIds;

    @Nullable
    private String key;

    @Nullable
    private String description;

    @Nullable
    private String systemRefId;


}
