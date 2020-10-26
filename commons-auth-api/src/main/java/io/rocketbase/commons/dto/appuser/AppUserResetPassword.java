package io.rocketbase.commons.dto.appuser;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"resetPassword"})
public class AppUserResetPassword implements Serializable {

    @NotNull
    private String resetPassword;
}
