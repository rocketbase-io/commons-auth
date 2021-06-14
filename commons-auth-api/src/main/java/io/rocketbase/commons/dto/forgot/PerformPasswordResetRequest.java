package io.rocketbase.commons.dto.forgot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * body for password change after forgot triggered
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
@Schema(description = "body for password change after forgot triggered")
public class PerformPasswordResetRequest implements Serializable {

    @NotNull
    private String verification;

    @NotNull
    private String password;
}
