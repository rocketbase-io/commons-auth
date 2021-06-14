package io.rocketbase.commons.dto.appinvite;

import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import lombok.*;

import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    @Size(max = 4000)
    @Nullable
    private String message;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotEmpty
    @Singular
    private List<String> roles;

    @Singular
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
}
