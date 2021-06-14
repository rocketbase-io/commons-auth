package io.rocketbase.commons.dto.registration;

import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import lombok.*;

import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
public class RegistrationRequest implements Serializable, HasKeyValue, HasFirstAndLastName {

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

    @Nullable
    private Map<String, String> keyValues;

    /**
     * optional parameter to overwrite system default
     * <p>
     * full qualified url to a custom UI that proceed the verification<br>
     * * ?verification=VALUE will get append
     */
    @Nullable
    private String verificationUrl;
}
