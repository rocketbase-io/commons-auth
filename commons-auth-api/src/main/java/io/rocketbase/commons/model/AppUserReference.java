package io.rocketbase.commons.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.rocketbase.commons.model.user.UserProfile;
import org.springframework.lang.Nullable;

import java.beans.Transient;
import java.io.Serializable;

/**
 * a short representation of a user<br>
 * will be used for user-search results for example<br>
 * important: will not keep secrets, audit oder security information like capabilities, passwords or groups
 */
@JsonDeserialize(as = SimpleAppUserReference.class)
public interface AppUserReference extends Serializable {

    String getId();

    @Nullable
    String getSystemRefId();

    String getUsername();

    String getEmail();

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    @Transient
    @Nullable
    String getFirstName();

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    @Transient
    @Nullable
    String getLastName();

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    @Transient
    @Nullable
    String getAvatar();

    @Nullable
    UserProfile getProfile();

    /**
     * fullname fallback if null use username
     *
     * @return {@link #getProfile().getFullName()} in case of null will return getUsername
     */
    @Transient
    default String getDisplayName() {
        String fullName = getProfile() != null ? getProfile().getFullName() : null;
        if (fullName == null) {
            return getUsername();
        }
        return fullName;
    }
}
