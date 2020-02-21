package io.rocketbase.commons.test.model;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.util.RolesAuthoritiesConverter;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppUserTestEntity implements AppUserEntity {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private String avatar;

    private List<String> roles;

    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    private boolean enabled;

    private Instant created;

    private Instant lastLogin;

    private Instant lastTokenInvalidation;

    @Override
    public void updateLastLogin() {
        this.lastLogin = Instant.now();
    }

    @Override
    public void updateLastTokenInvalidation() {
        this.lastTokenInvalidation = Instant.now();
    }


    @Override
    public AppUserTestEntity clone() {
        Map<String, String> copyedKeyValueMap = getKeyValueMap() != null ? new HashMap<>(ImmutableMap.copyOf(getKeyValueMap())) : null;
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
                .keyValueMap(copyedKeyValueMap)
                .build();
    }

    @Override
    public AppUserEntity addKeyValue(String key, String value) {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return RolesAuthoritiesConverter.convert(getRoles());
    }
}
