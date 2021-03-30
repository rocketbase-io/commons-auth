package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appteam.AppUserMembership;
import io.rocketbase.commons.model.user.SimpleUserSetting;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleAppUserToken extends SimpleAppUserReference implements AppUserToken {

    @Nullable
    private Set<AppGroupShort> groups;

    private Set<String> capabilities;

    @Nullable
    private AppUserMembership activeTeam;

    @Nullable
    private Map<String, String> keyValues = new HashMap<>();

    @Nullable
    private UserSetting setting;

    public SimpleAppUserToken(String id, String username, Set<String> capabilities) {
        super(id, username);
        this.capabilities = capabilities;
    }

    @Builder(builderMethodName = "builderToken")
    public SimpleAppUserToken(String id, String systemRefId, String username, String email, UserProfile profile, Set<AppGroupShort> groups, Set<String> capabilities, AppUserMembership activeTeam, Map<String, String> keyValues, UserSetting setting) {
        super(id, systemRefId, username, email, profile);
        this.groups = groups;
        this.capabilities = capabilities;
        this.activeTeam = activeTeam;
        this.keyValues = keyValues;
        this.setting = setting;
    }

    public SimpleAppUserToken(AppUserReference reference, Set<AppGroupShort> groups, Set<String> capabilities, AppUserMembership activeTeam, Map<String, String> keyValues) {
        super(reference.getId(), reference.getSystemRefId(), reference.getUsername(), reference.getEmail(), reference.getProfile());
        this.groups = groups;
        this.capabilities = capabilities;
        this.activeTeam = activeTeam;
        this.keyValues = keyValues;
    }

    public SimpleAppUserToken(AppUserToken other) {
        setId(other.getId());
        setSystemRefId(other.getSystemRefId());
        setUsername(other.getUsername());
        setEmail(other.getEmail());
        setProfile(other.getProfile());
        this.groups = other.getGroups() != null ? other.getGroups().stream().map(AppGroupShort::new).collect(Collectors.toSet()) : null;
        this.capabilities = other.getCapabilities() != null ? new TreeSet<>(other.getCapabilities()) : null;
        this.activeTeam = other.getActiveTeam() != null ? new AppUserMembership(other.getActiveTeam()) : null;
        this.keyValues = other.getKeyValues() != null ? new HashMap<>(other.getKeyValues()) : null;
        this.setting = other.getSetting() != null ? new SimpleUserSetting(other.getSetting()) : null;
    }

    @Override
    public String toString() {
        return "SimpleAppUserToken{" +
                "id='" + getId() + '\'' +
                ", systemRefId='" + getSystemRefId() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", profile=" + getProfile() +
                ", groups=" + groups +
                ", capabilities=" + capabilities +
                ", activeTeam=" + activeTeam +
                ", keyValues=" + keyValues +
                ", setting=" + setting +
                '}';
    }
}
