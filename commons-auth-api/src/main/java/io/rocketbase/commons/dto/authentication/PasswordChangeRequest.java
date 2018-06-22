package io.rocketbase.commons.dto.authentication;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"currentPassword", "newPassword"})
public class PasswordChangeRequest implements Serializable {

    @NotNull
    private String currentPassword;

    @NotNull
    private String newPassword;
}
