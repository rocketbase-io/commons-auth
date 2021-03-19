package io.rocketbase.commons.test.model;

import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
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
@EqualsAndHashCode(of = "id")
public class SimpleAppUserEntity implements AppUserEntity, AppUserReference {

    private String id;

    private String systemRefId;

    private String username;

    private String password;

    private String email;

    private Set<Long> capabilityIds;

    private Map<String, String> keyValues = new HashMap<>();

    private Set<Long> groupIds;

    private Long activeTeamId;

    private boolean enabled;

    private boolean locked;

    private Instant created;

    private String modifiedBy;

    private Instant modified;

    private Instant lastLogin;

    private Instant lastTokenInvalidation;

    private UserProfile profile;

    private UserSetting setting;

    @Override
    public void updateLastLogin() {
        lastLogin = Instant.now();
    }

    @Override
    public void updateLastTokenInvalidation() {
        lastTokenInvalidation = Instant.now();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppUserEntity)) return false;
        final AppUserEntity other = (AppUserEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
