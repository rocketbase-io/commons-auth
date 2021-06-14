package io.rocketbase.commons.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * body to change username
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "body to change username")
public class UsernameChangeRequest implements Serializable {

    @NotNull
    private String newUsername;

}
