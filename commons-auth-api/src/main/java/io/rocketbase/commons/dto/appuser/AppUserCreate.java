package io.rocketbase.commons.dto.appuser;

import io.rocketbase.commons.dto.CommonDtoSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserCreate implements Serializable {

    @NotNull
    private String username;

    @NotNull
    @Pattern(message = CommonDtoSettings.PASSWORD_MESSAGE, regexp = CommonDtoSettings.PASSWORD_REGEX)
    private String password;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    private String avatar;

    @NotNull
    private Boolean admin;

    @NotNull
    private Boolean enabled;
}
