package io.rocketbase.commons.dto.appteam;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
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
public class QueryAppTeam implements Serializable {

    @Nullable
    private Set<Long> ids;

    @Nullable
    private String name;

    @Nullable
    private String description;

    @Nullable
    private Boolean personal;

    @Nullable
    private String systemRefId;

}
