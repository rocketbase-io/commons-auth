package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@Document(collection = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserEntity extends AppUser {


    @NotNull
    @Indexed(unique = true)
    private String id;

    private String username;

    private String firstName;

    private String lastName;

    @NotNull
    private String password;

    @NotNull
    @Indexed(unique = true)
    @Email
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
