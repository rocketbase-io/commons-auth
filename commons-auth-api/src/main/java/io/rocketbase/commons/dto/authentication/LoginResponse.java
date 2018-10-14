package io.rocketbase.commons.dto.authentication;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {

    private JwtTokenBundle jwtTokenBundle;
    private AppUserRead user;

}
