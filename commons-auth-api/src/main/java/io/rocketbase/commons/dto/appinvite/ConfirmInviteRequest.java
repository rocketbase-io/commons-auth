package io.rocketbase.commons.dto.appinvite;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
public class ConfirmInviteRequest implements Serializable {

    @NotNull
    private String inviteId;

    @NotNull
    private String username;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;
}
