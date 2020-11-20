package io.rocketbase.commons.dto.appinvite;

import io.rocketbase.commons.model.HasFirstAndLastName;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
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
