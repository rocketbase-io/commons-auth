package io.rocketbase.commons.dto.appinvite;

import lombok.*;

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

    private String invitor;
    private String email;
    private Boolean expired;

    /**
     * search for given key and value with exact match ignore cases
     */
    @Singular
    private Map<String, String> keyValues;
}
