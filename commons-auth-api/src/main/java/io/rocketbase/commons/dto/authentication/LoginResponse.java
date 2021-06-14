package io.rocketbase.commons.dto.authentication;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * response after successful login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "response after successful login")
public class LoginResponse implements Serializable {

    @NotNull
    private JwtTokenBundle jwtTokenBundle;

    @NotNull
    private AppUserRead user;

}
