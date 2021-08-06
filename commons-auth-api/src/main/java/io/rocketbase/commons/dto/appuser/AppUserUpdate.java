package io.rocketbase.commons.dto.appuser;

import io.rocketbase.commons.model.HasKeyValue;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * null properties mean let value as it is
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserUpdate implements Serializable, HasKeyValue {

    @Nullable
    private Set<Long> capabilityIds;

    @Nullable
    private Set<Long> groupIds;

    @Nullable
    private UserProfile profile;

    @Nullable
    private UserSetting setting;

    /**
     * will removed key that have value of null <br>
     * will only add/replace new/existing key values<br>
     * not mentioned key will still stay the same
     */
    @Nullable
    private Map<String, String> keyValues;

    @Nullable
    private Boolean enabled;

    @Nullable
    private Boolean locked;

    @Nullable
    private Long activeTeamId;
}
