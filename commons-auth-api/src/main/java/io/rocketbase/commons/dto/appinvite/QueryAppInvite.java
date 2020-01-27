package io.rocketbase.commons.dto.appinvite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
}
