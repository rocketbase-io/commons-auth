package io.rocketbase.commons.dto.appuser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.rocketbase.commons.dto.appcapability.AppCapabilityShort;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appteam.AppUserMembership;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.model.SimpleAppUserReference;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
import lombok.*;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@JsonDeserialize(as = AppUserRead.class)
public class AppUserRead implements AppUserReference, Serializable {

    private String id;

    @Nullable
    private String systemRefId;

    private String username;

    private String email;

    private Set<AppCapabilityShort> capabilities;

    @Nullable
    private Set<AppGroupShort> groups;

    @Nullable
    private AppUserMembership activeTeam;

    @Nullable
    private Map<String, String> keyValues;

    private boolean enabled;

    private boolean locked;

    private Instant created;

    private String modifiedBy;

    private Instant modified;

    @Nullable
    private Instant lastLogin;

    @Nullable
    private UserProfile profile;

    @Nullable
    private UserSetting setting;

    @JsonIgnore
    public AppUserReference toReference() {
        return SimpleAppUserReference.builder()
                .id(getId())
                .systemRefId(getSystemRefId())
                .username(getUsername())
                .email(getEmail())
                .profile(getProfile())
                .build();
    }

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    @Override
    public String getAvatar() {
        return profile != null ? profile.getAvatar() : null;
    }

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    @Override
    public String getFirstName() {
        return profile != null ? profile.getFirstName() : null;
    }

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    @Override
    public String getLastName() {
        return profile != null ? profile.getLastName() : null;
    }


}
