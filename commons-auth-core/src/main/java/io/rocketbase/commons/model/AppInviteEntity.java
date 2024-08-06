package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public interface AppInviteEntity extends EntityWithKeyValue<AppInviteEntity>, HasFirstAndLastName {

    String getId();

    void setId(String id);

    String getInvitor();

    void setInvitor(@NotNull String invitor);

    String getMessage();

    void setMessage(String message);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    String getEmail();

    void setEmail(@NotNull @Email String email);

    List<String> getRoles();

    void setRoles(List<String> roles);

    Instant getCreated();

    Instant getExpiration();

    void setExpiration(@NotNull Instant expiration);

    /**
     * fullname fallback if null use email
     */
    @JsonIgnore
    default String getDisplayName() {
        String fullName = getFullName();
        if (fullName == null) {
            return getEmail();
        }
        return fullName;
    }
}
