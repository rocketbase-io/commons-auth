package io.rocketbase.commons.dto.appinvite;

import io.rocketbase.commons.model.HasFirstAndLastName;
import lombok.*;

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
public class InviteRequest implements Serializable, HasFirstAndLastName {

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
    private String message;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotEmpty
    @Singular
    private List<String> roles;

    @Singular
    private Map<String, String> keyValues;

    /**
     * optional parameter to overwrite system default
     * <p>
     * full qualified url to a custom UI that proceed the invite<br>
     * ?inviteId=VALUE will get append
     */
    private String inviteUrl;
}