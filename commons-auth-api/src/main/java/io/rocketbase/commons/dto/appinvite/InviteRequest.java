package io.rocketbase.commons.dto.appinvite;

import io.rocketbase.commons.dto.appteam.AppTeamInvite;
import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteRequest implements Serializable, HasFirstAndLastName, HasKeyValue {

    /**
     * name of invitor that will get displayed within email + form
     */
    @NotNull
    @Size(max = 255)
    private String invitor;

    /**
     * optional message to add to invited person
     */
    @Nullable
    @Size(max = 2000)
    private String message;

    @Nullable
    @Size(max = 100)
    private String firstName;

    @Nullable
    @Size(max = 100)
    private String lastName;

    @NotNull
    @Email
    @Size(max = 255)
    private String email;

    @NotEmpty
    private Set<Long> capabilityIds;

    @Nullable
    private Set<Long> groupIds;

    @Nullable
    private Map<String, String> keyValues;

    /**
     * optional parameter to overwrite system default
     * <p>
     * full qualified url to a custom UI that proceed the invite<br>
     * ?inviteId=VALUE will get append
     */
    @Nullable
    private String inviteUrl;

    @Nullable
    private AppTeamInvite teamInvite;
}
