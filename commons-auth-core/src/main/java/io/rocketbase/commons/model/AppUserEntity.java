package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * entity instance of user that is used by persistence layers internally
 */
public interface AppUserEntity extends Serializable, EntityWithKeyValue<AppUserEntity>, AppUserReference, EntityWithAudit, EntityWithSystemRefId {

    String getId();

    void setId(@Size(max = 36) String id);

    String getUsername();

    void setUsername(@Size(max = 255) String username);

    String getPassword();

    void setPassword(String password);

    String getEmail();

    void setEmail(@Size(max = 255) @Email String email);

    void setEnabled(boolean enabled);

    boolean isEnabled();

    void setLocked(boolean locked);

    boolean isLocked();

    /**
     * @return null or date of last login
     */
    Instant getLastLogin();

    /**
     * used for analytics of inactive user xyz<br>
     * should update lastLogin with TimeZone UTC
     */
    void updateLastLogin();

    /**
     * @return null or date of last token invalidation with TimeZone UTC
     */
    Instant getLastTokenInvalidation();

    /**
     * used to mark token that are issued before as invalid<br>
     * should update lastTokenInvalidation with TimeZone UTC
     */
    void updateLastTokenInvalidation();

    Set<Long> getCapabilityIds();

    void setCapabilityIds(Set<Long> capabilityIds);

    Set<Long> getGroupIds();

    void setGroupIds(Set<Long> groupIds);

    Long getActiveTeamId();

    void setActiveTeamId(Long teamId);

    UserProfile getProfile();

    void setProfile(UserProfile userProfile);

    UserSetting getSetting();

    void setSetting(UserSetting userSetting);

    /**
     * convert current instance to a simple reference copy
     *
     * @return fresh created reference based on this {@link AppUserEntity}
     */
    @JsonIgnore
    default AppUserReference toReference() {
        return SimpleAppUserReference.builder()
                .id(getId())
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
    default String getAvatar() {
        return getProfile() != null ? getProfile().getAvatar() : null;
    }

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    default String getFirstName() {
        return getProfile() != null ? getProfile().getFirstName() : null;
    }

    /**
     * deprecated since 5.0.0<br>
     * should be removed - use UserProfile instead
     */
    @Deprecated
    default String getLastName() {
        return getProfile() != null ? getProfile().getLastName() : null;
    }


}
