package io.rocketbase.commons.dto.forgot;

import lombok.*;

import javax.validation.constraints.NotNull;
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
    private String password;
}
