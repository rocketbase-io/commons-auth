package io.rocketbase.commons.model;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private LocalDateTime created;

    private LocalDateTime lastLogin;

    private LocalDateTime lastTokenInvalidation;

    @Override
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public void updateLastTokenInvalidation() {
        this.lastTokenInvalidation = LocalDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public AppUser addKeyValue(String key, String value) {
        checkKeyValue(key, value);
        keyValueMap.put(key.toLowerCase(), value);
        return this;
    }

    @Override
    public void removeKeyValue(String key) {
        keyValueMap.remove(key.toLowerCase());
    }

    @Override
    public Map<String, String> getKeyValues() {
        return keyValueMap != null ? ImmutableMap.copyOf(keyValueMap) : null;
    }

    @Override
    public boolean hasKeyValue(String key) {
        return keyValueMap != null && key != null && keyValueMap.containsKey(key.toLowerCase());
    }

    @Override
    public String getKeyValue(String key) {
        return keyValueMap != null && key != null ? keyValueMap.getOrDefault(key.toLowerCase(), null) : null;
    }

}
