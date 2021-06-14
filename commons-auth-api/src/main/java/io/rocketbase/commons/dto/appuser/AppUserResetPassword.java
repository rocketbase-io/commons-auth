package io.rocketbase.commons.dto.appuser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * post body for rest-password
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"resetPassword"})
@Schema(description = "post body for rest-password")
public class AppUserResetPassword implements Serializable {

    @NotNull
    private String resetPassword;
}
