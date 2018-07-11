package io.rocketbase.commons.dto.appuser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * simplified AppUser without keyValues, password, audit etc...<br>
 * used to store a simple representation as a copy of AppUser
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserReference implements Serializable {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String avatar;
}
