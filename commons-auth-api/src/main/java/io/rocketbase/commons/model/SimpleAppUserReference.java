package io.rocketbase.commons.model;

import io.rocketbase.commons.model.user.SimpleUserProfile;
import io.rocketbase.commons.model.user.UserProfile;
import lombok.*;
import org.springframework.lang.Nullable;

/**
 * simplified AppUserEntity without keyValues, password, audit etc...<br>
 * used to store a simple representation as a copy of AppUserEntity in mongo or elsewhere
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SimpleAppUserReference implements AppUserReference {

    private String id;

    @Nullable
    private String systemRefId;

    private String username;

    private String email;

    @Nullable
    private UserProfile profile;

    public SimpleAppUserReference(SimpleAppUserReference other) {
        this.id = other.id;
        this.systemRefId = other.systemRefId;
        this.username = other.username;
        this.email = other.email;
        this.profile = other.profile != null ? new SimpleUserProfile(other.getProfile()) : null;
    }

    public SimpleAppUserReference(String id, String username) {
        this.id = id;
        this.username = username;
    }

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    @Override
    public String getAvatar() {
        return getProfile() != null ? getProfile().getAvatar() : null;
    }

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    @Override
    public String getFirstName() {
        return getProfile() != null ? getProfile().getFirstName() : null;
    }

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    @Override
    public String getLastName() {
        return getProfile() != null ? getProfile().getLastName() : null;
    }
}
