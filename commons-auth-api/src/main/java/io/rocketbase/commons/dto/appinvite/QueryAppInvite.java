package io.rocketbase.commons.dto.appinvite;

import io.rocketbase.commons.model.HasKeyValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Map;

/**
 * query object to find invites
 * string properties mean search like ignore cases
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAppInvite implements Serializable, HasKeyValue {

    @Nullable
    private String invitor;

    @Nullable
    private String email;

    @Nullable
    private Boolean expired;

    /**
     * search for given key and value with exact match ignore cases
     */
    @Nullable
    private Map<String, String> keyValues;

    @Nullable
    private Long teamId;

    @Nullable
    private String systemRefId;

}
