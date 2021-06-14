package io.rocketbase.commons.dto.appuser;

import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * post body for update user. <b>null properties mean let value as it is</b>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
@Schema(description = "post body for update user. <b>null properties mean let value as it is</b>")
public class AppUserUpdate implements Serializable, HasKeyValue, HasFirstAndLastName {

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
    @Schema(description = "will removed key that have value of null.\n" +
            "will only add/replace new/existing key values.\n" +
            "not mentioned key will still stay the same")
    private Map<String, String> keyValues;

    @Nullable
    private Boolean enabled;
}
