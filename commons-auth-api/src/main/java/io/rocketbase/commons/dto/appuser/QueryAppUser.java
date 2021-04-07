package io.rocketbase.commons.dto.appuser;

import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * query object to find user
 * string properties mean search like ignore cases
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAppUser implements Serializable, HasKeyValue, HasFirstAndLastName {

    @Nullable
    private String username;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    private String email;

    @Nullable
    private String systemRefId;

    /**
     * search for given key and value with exact match
     */
    @Nullable
    private Map<String, String> keyValues;

    /**
     * searches for all properties containing text
     */
    @Nullable
    private String freetext;

    /**
     * exact capability search
     */
    @Nullable
    private Set<Long> capabilityIds;

    /**
     * user is member of group
     */
    @Nullable
    private Set<Long> groupIds;

    @Nullable
    private Boolean enabled;

}
