package io.rocketbase.commons.dto.appuser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * null properties mean let value as it is
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserUpdate implements Serializable {

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String email;

    private String avatar;

    private List<String> roles;

    private Boolean enabled;
}
