package io.rocketbase.commons.model;

import io.rocketbase.commons.util.RolesAuthoritiesConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "USER", uniqueConstraints = {
        @UniqueConstraint(name = "UK_USER_USERNAME", columnNames = {"username"}),
        @UniqueConstraint(name = "UK_USER_EMAIL", columnNames = {"email"})})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserJpaEntity implements AppUserEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @NotNull
    @Column(length = 20, nullable = false)
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    @Column(name = "role")
    private List<String> roles;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_KEYVALUE_PAIRS", joinColumns = @JoinColumn(name = "id"))
    @MapKeyColumn(name = "FIELD_KEY", length = 50)
    @Column(name = "FIELD_VALUE", length = 4000)
    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    private boolean enabled;

    @CreatedDate
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
