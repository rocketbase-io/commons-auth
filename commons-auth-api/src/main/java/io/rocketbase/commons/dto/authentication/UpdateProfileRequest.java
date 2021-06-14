package io.rocketbase.commons.dto.authentication;

import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import lombok.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest implements Serializable, HasKeyValue, HasFirstAndLastName {

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    private String avatar;

    /**
     * will removed key that have value of null <br>
     * will only add/replace new/existing key values<br>
     * not mentioned key will still stay the same
     */
    @Nullable
    @Singular
    private Map<String, String> keyValues;

    public static UpdateProfileRequest init(AppUserReference user) {
        return UpdateProfileRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .keyValues(new HashMap<>())
                .build();
    }

}
