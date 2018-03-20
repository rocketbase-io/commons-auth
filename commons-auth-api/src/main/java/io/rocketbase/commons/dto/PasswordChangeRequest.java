package io.rocketbase.commons.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    @Pattern(message = CommonDtoSettings.PASSWORD_MESSAGE, regexp = CommonDtoSettings.PASSWORD_REGEX)
    private String newPassword;
}
