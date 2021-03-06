package io.rocketbase.commons.dto.appuser;

import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * post body to create a new user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
@Schema(description = "post body to create a new user")
public class AppUserCreate implements Serializable, HasFirstAndLastName, HasKeyValue {

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
