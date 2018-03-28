package io.rocketbase.commons.dto.forgot;

import io.rocketbase.commons.dto.CommonDtoSettings;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
public class PerformPasswordResetRequest implements Serializable {

    @NotNull
    private String verification;

    @NotNull
    @Pattern(message = CommonDtoSettings.PASSWORD_MESSAGE, regexp = CommonDtoSettings.PASSWORD_REGEX)
    private String password;
}
