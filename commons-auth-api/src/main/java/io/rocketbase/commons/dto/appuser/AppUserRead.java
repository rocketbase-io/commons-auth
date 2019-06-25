package io.rocketbase.commons.dto.appuser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.SimpleAppUserReference;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppUserRead implements AppUserToken, Serializable {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String avatar;

    private List<String> roles;

    private Map<String, String> keyValues;

    private boolean enabled;

    private LocalDateTime created;

    private LocalDateTime lastLogin;

    @JsonIgnore
    public AppUserReference toReference() {
        return SimpleAppUserReference.builder()
                .id(getId())
                .username(getUsername())
                .firstName(getFirstName())
                .lastName(getLastName())
                .email(getEmail())
                .avatar(getAvatar())
                .build();
    }

    public boolean hasKeyValue(String key) {
        return keyValues != null && key != null && keyValues.containsKey(key.toLowerCase());
    }

    public String getKeyValue(String key) {
        return keyValues != null && key != null ? keyValues.getOrDefault(key.toLowerCase(), null) : null;
    }
}
