package io.rocketbase.commons.dto.appinvite;

import lombok.*;

import javax.annotation.Nullable;
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
public class QueryAppInvite implements Serializable {

    @Nullable
    private String invitor;
    @Nullable
    private String email;
    @Nullable
    private Boolean expired;

    /**
     * search for given key and value with exact match ignore cases
     */
    @Singular
    @Nullable
    private Map<String, String> keyValues;
}
