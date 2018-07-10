package io.rocketbase.commons.model;

import lombok.*;

import java.io.Serializable;

/**
 * simplified {@link AppUser} without keyValues pairs, password etc...<br>
 * used to store a simple representation as a copy of {@link AppUser}
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
