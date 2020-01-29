package io.rocketbase.commons.dto.appuser;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * query object to find user
 * string properties mean search like ignore cases
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAppUser implements Serializable {

    private String username;
    private String firstName;
    private String lastName;
    private String email;

    /**
     * search for given key and value with exact match ignore cases
     */
    @Singular
    private Map<String, String> keyValues;

    /**
     * searches for all properties containing text
     */
    private String freetext;
    /**
     * used needs to have role ignore cases
     */
    private String hasRole;
    private Boolean enabled;
}
