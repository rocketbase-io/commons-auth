package io.rocketbase.commons.dto;

import lombok.*;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
public class RegistrationRequest {

    @NotNull
    private String username;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Pattern(message = CommonDtoSettings.PASSWORD_MESSAGE, regexp = CommonDtoSettings.PASSWORD_REGEX)
    private String password;
}
