package io.rocketbase.commons.model;

import io.rocketbase.commons.util.RolesAuthoritiesConverter;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "co_user", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_username", columnNames = {"username"}),
        @UniqueConstraint(name = "uk_user_email", columnNames = {"email"})})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppUserJpaEntity implements AppUserEntity {

    @Id
    @Column(length = 36)
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

    @Column(length = 2000)
    private String avatar;

    @ElementCollection
    @CollectionTable(
            name = "co_user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_roles", columnNames = {"user_id", "role"})
    )
    @Column(name = "role", length = 20, nullable = false)
    private List<String> roles;

    @ElementCollection
    @CollectionTable(
            name = "co_user_keyvalue_pairs",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_keyvalue_pairs", columnNames = {"user_id", "field_key"}),
            indexes = @Index(name = "idx_user_keyvalue_pairs", columnList = "user_id")
    )
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 4000, nullable = false)
    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    private boolean enabled;

    @NotNull
    @CreatedDate
    @Column(nullable = false)
    private Instant created;

    private Instant lastLogin;

    private Instant lastTokenInvalidation;

    public void updateLastLogin() {
        this.lastLogin = Instant.now();
    }

    @Override
    public void updateLastTokenInvalidation() {
        this.lastTokenInvalidation = Instant.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return RolesAuthoritiesConverter.convert(getRoles());
    }

    @Override
    public Map<String, String> getKeyValues() {
        return keyValueMap;
    }
}
