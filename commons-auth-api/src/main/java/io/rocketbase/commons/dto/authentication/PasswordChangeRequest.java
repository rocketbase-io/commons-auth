package io.rocketbase.commons.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * body for password change
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"currentPassword", "newPassword"})
@Schema(description = "body for password change")
public class PasswordChangeRequest implements Serializable {

    @NotNull
    private String currentPassword;

    @NotNull
    private String newPassword;
}
