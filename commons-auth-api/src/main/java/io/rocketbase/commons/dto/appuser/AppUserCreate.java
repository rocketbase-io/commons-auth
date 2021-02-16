package io.rocketbase.commons.dto.appuser;

import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
public class AppUserCreate implements Serializable, HasFirstAndLastName, HasKeyValue {

    @NotNull
    @Size(max = 255)
    private String username;

    @NotNull
    private String password;

    @Nullable
    @Size(max = 100)
    private String firstName;

    @Nullable
    @Size(max = 100)
    private String lastName;

    @NotNull
    @Email
    @Size(max = 255)
    private String email;

    @Nullable
    @Size(max = 2000)
    private String avatar;

    @Nullable
    private Map<String, String> keyValues;

    @Builder.Default
    private boolean enabled = true;

    @Nullable
    private Set<Long> capabilityIds;

    @Nullable
    private Set<Long> groupIds;
}
