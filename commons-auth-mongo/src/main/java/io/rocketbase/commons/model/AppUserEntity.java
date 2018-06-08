package io.rocketbase.commons.model;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Document(collection = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserEntity extends AppUser {

    @Id
    private String id;

    @NotNull
    @Indexed(unique = true)
    private String username;

    private String firstName;

    private String lastName;

    @NotNull
    private String password;

    @NotNull
    @Indexed(unique = true)
    @Email
    private String email;

    private String avatar;

    private List<String> roles;

    private boolean enabled;

    @CreatedDate
    private LocalDateTime created;

    private LocalDateTime lastLogin;

    private LocalDateTime lastTokenInvalidation;

    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    @Override
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    @Override
    public void updateLastTokenInvalidation() {
        this.lastTokenInvalidation = LocalDateTime.now();
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
        return ImmutableMap.copyOf(keyValueMap);
    }
}
