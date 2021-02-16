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
public interface AppInviteEntity extends Serializable, EntityWithKeyValue<AppInviteEntity>, HasFirstAndLastName {

    Long getId();

    void setId(Long id);

    String getInvitor();

    @Size(max = 255)
    void setInvitor(String invitor);

    String getMessage();

    @Size(max = 4000)
    void setMessage(String message);

    String getFirstName();

    @Size(max = 100)
    void setFirstName(String firstName);

    String getLastName();

    @Size(max = 100)
    void setLastName(String lastName);

    String getEmail();

    @Size(max = 255)
    void setEmail(String email);

    Set<Long> getCapabilityIds();

    void setCapabilityIds(Set<Long> capabilityIds);

    Set<Long> getGroupIds();

    void setGroupIds(Set<Long> groupIds);

    AppTeamInvite getTeamInvite();

    void setTeamInvite(AppTeamInvite team);

    void setExpiration(Instant expiration);

    Instant getExpiration();

    Instant getCreated();

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
