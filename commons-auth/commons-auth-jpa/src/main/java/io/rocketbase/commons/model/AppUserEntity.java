package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "USER", uniqueConstraints = {
        @UniqueConstraint(name = "UK_USER_USERNAME", columnNames = {"username"}),
        @UniqueConstraint(name = "UK_USER_EMAIL", columnNames = {"email"})})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserEntity extends AppUser {

    @Id
    private String id;

    @NotNull
    private String username;

    private String firstName;

    private String lastName;

    @NotNull
    private String password;

    @NotNull
    @Email
    private String email;

    @ElementCollection
    @CollectionTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    @Column(name = "role")
    private List<String> roles;

    private boolean enabled;

    @CreatedDate
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
