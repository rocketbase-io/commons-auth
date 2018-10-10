package io.rocketbase.commons.test.model;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    private boolean enabled;

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
}
