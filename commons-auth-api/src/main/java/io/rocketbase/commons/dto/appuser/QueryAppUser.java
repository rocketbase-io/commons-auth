package io.rocketbase.commons.dto.appuser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
     * searches for all properties containing text
     */
    private String freetext;
    private Boolean enabled;
}
