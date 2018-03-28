package io.rocketbase.commons.dto.authentication;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
public class LoginRequest implements Serializable {

    @NotNull
    private String username;

    @NotNull
    private String password;
}
