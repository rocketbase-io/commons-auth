package io.rocketbase.commons.dto.appinvite;

import io.rocketbase.commons.dto.appcapability.AppCapabilityShort;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appteam.AppTeamInvite;
import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import lombok.*;
import org.springframework.lang.Nullable;

import java.beans.Transient;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppInviteRead implements HasKeyValue, HasFirstAndLastName {
    private Long id;

    private String invitor;

    private String message;

    private String firstName;

    private String lastName;

    private String email;

    private Set<AppCapabilityShort> capabilities;

    private Map<String, String> keyValues;

    @Nullable
    private AppTeamInvite teamInvite;

    @Nullable
    private Set<AppGroupShort> groups;

    private Instant created;

    private Instant expiration;

    /**
     * fullname fallback if null use email
     *
     * @return {@link #getFullName()} in case of null will return getEmail
     */
    @Transient
    public String getDisplayName() {
        String fullName = getFullName();
        if (fullName == null) {
            return getEmail();
        }
        return fullName;
    }

}
