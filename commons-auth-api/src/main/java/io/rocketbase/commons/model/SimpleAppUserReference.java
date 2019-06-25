package io.rocketbase.commons.model;

import lombok.*;

/**
 * simplified AppUser without keyValues, password, audit etc...<br>
 * used to store a simple representation as a copy of AppUser in mongo or elsewhere
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SimpleAppUserReference implements AppUserReference {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String avatar;
}
