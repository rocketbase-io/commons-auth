package io.rocketbase.commons.test.model;

import io.rocketbase.commons.dto.appteam.AppTeamInvite;
import io.rocketbase.commons.model.AppInviteEntity;
import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAppInviteEntity implements AppInviteEntity {

    private Long id;

    private String systemRefId;

    private String invitor;

    private String message;

    private String firstName;

    private String lastName;

    private String email;

    private Set<Long> capabilityIds;

    @Singular
    private Map<String, String> keyValues = new HashMap<>();

    private Set<Long> groupIds;

    private Instant expiration;

    private Instant created;

    private String modifiedBy;

    private Instant modified;

    private AppTeamInvite teamInvite;

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppInviteEntity)) return false;
        final AppInviteEntity other = (AppInviteEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
