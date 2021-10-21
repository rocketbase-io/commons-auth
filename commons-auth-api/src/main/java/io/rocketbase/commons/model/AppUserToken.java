package io.rocketbase.commons.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appteam.AppUserMembership;
import io.rocketbase.commons.model.user.UserSetting;
import io.rocketbase.commons.util.Nulls;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * extended {@link AppUserReference} with roles, keyValues and groups and activeTeam<br>
 * all it's information will get stored within the jwt-token for example
 */
@JsonDeserialize(as = SimpleAppUserToken.class)
public interface AppUserToken extends AppUserReference, HasKeyValue {

    @Nullable
    String getIdentityProvider();

    Set<String> getCapabilities();

    @Nullable
    Set<AppGroupShort> getGroups();

    @Nullable
    AppUserMembership getActiveTeam();

    @Nullable
    UserSetting getSetting();

    /**
     * removed not allowed capabilities (used for limited client's for example) or token refreshes
     */
    default AppUserToken retainCapabilities(Set<String> allowedCapabilities) {
        if (getCapabilities() == null) {
            return this;
        }
        getCapabilities().retainAll(Nulls.notNull(allowedCapabilities));
        return this;
    }

}
