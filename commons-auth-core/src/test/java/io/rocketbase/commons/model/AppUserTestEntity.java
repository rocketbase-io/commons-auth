package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


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

    private String avatar;

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

    @Override
    public AppUserTestEntity clone() {
        return AppUserTestEntity.builder()
                .id(getId())
                .username(getUsername())
                .password(getPassword())
                .email(getEmail())
                .enabled(isEnabled())
                .roles(getRoles() != null ? getRoles().stream().map(r -> String.valueOf(r)).collect(Collectors.toList()) : null)
                .firstName(getFirstName())
                .lastName(getLastName())
                .avatar(getAvatar())
                .created(getCreated())
                .lastLogin(getLastLogin())
                .lastTokenInvalidation(getLastTokenInvalidation())
                .build();
    }
}
