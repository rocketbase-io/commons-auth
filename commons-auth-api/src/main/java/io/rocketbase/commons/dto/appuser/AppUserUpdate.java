package io.rocketbase.commons.dto.appuser;

import lombok.*;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * null properties mean let value as it is
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
public class AppUserUpdate implements Serializable {

    @Nullable
    private String password;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    private String avatar;

    @Nullable
    private List<String> roles;

    /**
     * will removed key that have value of null <br>
     * will only add/replace new/existing key values<br>
     * not mentioned key will still stay the same
     */
    @Singular
    @Nullable
    private Map<String, String> keyValues;

    @Nullable
    private Boolean enabled;
}
