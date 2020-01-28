package io.rocketbase.commons.dto.appuser;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
public class AppUserCreate implements Serializable {

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    private String avatar;

    @Singular
    private Map<String, String> keyValues;

    @NotNull
    private Boolean admin;

    @NotNull
    private Boolean enabled;
}
