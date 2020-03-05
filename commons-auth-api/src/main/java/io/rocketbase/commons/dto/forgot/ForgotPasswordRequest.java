package io.rocketbase.commons.dto.forgot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest implements Serializable {

    @Nullable
    private String username;

    @Email
    @Nullable
    private String email;

    /**
     * optional parameter to overwrite system default
     * <p>
     * full qualified url to a custom UI that proceed the password reset<br>
     * * ?verification=VALUE will get append
     */
    @Nullable
    private String resetPasswordUrl;
}
