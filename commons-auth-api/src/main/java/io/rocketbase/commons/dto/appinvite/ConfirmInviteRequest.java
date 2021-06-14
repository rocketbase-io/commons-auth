package io.rocketbase.commons.dto.appinvite;

import io.rocketbase.commons.model.HasFirstAndLastName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * post body during invite confirm progress
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
@Schema(description = "post body during invite confirm progress")
public class ConfirmInviteRequest implements Serializable, HasFirstAndLastName {

    @NotNull
    private String inviteId;

    @NotNull
    private String username;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;
}
