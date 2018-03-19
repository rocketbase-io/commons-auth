package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserTestEntity extends AppUser {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private List<String> roles;

    private boolean enabled;

    private LocalDateTime created;

    private LocalDateTime lastLogin;

    private LocalDateTime lastTokenInvalidation;

    @Override
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    @Override
    public void updateLastTokenInvalidation() {
        this.lastTokenInvalidation = LocalDateTime.now();
    }
}
