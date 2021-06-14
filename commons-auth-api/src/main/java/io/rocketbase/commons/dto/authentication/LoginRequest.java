package io.rocketbase.commons.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * body for login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
@Schema(description = "body for login")
public class LoginRequest implements Serializable {

    @NotNull
    private String username;

    @NotNull
    private String password;
}
