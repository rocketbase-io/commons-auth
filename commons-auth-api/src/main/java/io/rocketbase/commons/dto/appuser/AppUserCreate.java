package io.rocketbase.commons.dto.appuser;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
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

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @NotNull
    @Email
    private String email;

    @Nullable
    private String avatar;

    @Singular
    @Nullable
    private Map<String, String> keyValues;

    @Nullable
    private Boolean admin;

    @Singular
    @Nullable
    private List<String> roles;

    @Builder.Default
    private boolean enabled = true;
}
