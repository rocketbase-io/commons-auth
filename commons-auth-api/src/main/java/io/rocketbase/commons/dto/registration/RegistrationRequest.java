package io.rocketbase.commons.dto.registration;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
public class RegistrationRequest implements Serializable {

    @NotNull
    private String username;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    private Map<String, String> keyValues;
}
