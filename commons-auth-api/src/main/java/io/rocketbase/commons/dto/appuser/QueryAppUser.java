package io.rocketbase.commons.dto.appuser;

import lombok.*;

import javax.annotation.Nullable;
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

    @Nullable
    private String username;
    @Nullable
    private String firstName;
    @Nullable
    private String lastName;
    @Nullable
    private String email;

    /**
     * search for given key and value with exact match ignore cases
     */
    @Singular
    @Nullable
    private Map<String, String> keyValues;

    /**
     * searches for all properties containing text
     */
    @Nullable
    private String freetext;
    /**
     * used needs to have role ignore cases
     */
    @Nullable
    private String hasRole;
    @Nullable
    private Boolean enabled;
}
