package io.rocketbase.commons.dto.appclient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Set;

/**
 * query object to find {@link AppClientRead}
 * string properties mean search like ignore cases
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAppClient implements Serializable {

    @Nullable
    private Set<Long> ids;

    @Nullable
    private String name;

    @Nullable
    private String systemRefId;

    @Nullable
    private Set<Long> capabilityIds;

    @Nullable
    private String redirectUrl;

    @Nullable
    private String description;


}
