package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.appteam.AppTeamInvite;

import javax.validation.constraints.Size;
import java.beans.Transient;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * entity instance of invite that is used by persistence layers internally
 *
 */
public interface AppInviteEntity extends Serializable, EntityWithKeyValue<AppInviteEntity>, HasFirstAndLastName, EntityWithAudit, EntityWithSystemRefId {

    Long getId();

    void setId(Long id);

    String getInvitor();

    void setInvitor(@Size(max = 255) String invitor);

    String getMessage();

    void setMessage(@Size(max = 4000) String message);

    String getFirstName();

    void setFirstName(@Size(max = 100) String firstName);

    String getLastName();

    void setLastName(@Size(max = 100) String lastName);

    String getEmail();

    void setEmail(@Size(max = 255) String email);

    Set<Long> getCapabilityIds();

    void setCapabilityIds(Set<Long> capabilityIds);

    Set<Long> getGroupIds();

    void setGroupIds(Set<Long> groupIds);

    AppTeamInvite getTeamInvite();

    void setTeamInvite(AppTeamInvite team);

    void setExpiration(Instant expiration);

    Instant getExpiration();

    /**
     * fullname fallback if null use email
     *
     * @return {@link #getFullName()} in case of null will return getEmail
     */
    @Transient
    default String getDisplayName() {
        String fullName = getFullName();
        if (fullName == null) {
            return getEmail();
        }
        return fullName;
    }

}
