package io.rocketbase.commons.dto.forgot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import java.io.Serializable;

/**
 * body of password forgot process
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "body of password forgot process")
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
    @Schema(description = "optional parameter to overwrite system default\n" +
            "full qualified url to a custom UI that proceed the password reset.\n" +
            "* ?verification=VALUE will get append")
    private String resetPasswordUrl;


    /**
     * please use resetPasswordUrl will get removed in future
     */
    @Deprecated
    @Nullable
    @Schema(description = "please use resetPasswordUrl will get removed in future", deprecated = true)
    private String verificationUrl;
}
