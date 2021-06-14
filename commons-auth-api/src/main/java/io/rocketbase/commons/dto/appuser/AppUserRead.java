package io.rocketbase.commons.dto.appuser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.SimpleAppUserReference;
import lombok.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonDeserialize(as = AppUserRead.class)
public class AppUserRead implements AppUserToken, Serializable {

    private String id;

    private String username;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    private String email;

    @Nullable
    private String avatar;

    private List<String> roles;

    @Nullable
    private Map<String, String> keyValues;

    private boolean enabled;

    private Instant created;

    @Nullable
    private Instant lastLogin;

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

}
