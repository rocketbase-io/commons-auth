package io.rocketbase.commons.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * body for email change
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "body for email change")
public class EmailChangeRequest implements Serializable {

    @NotNull
    @Email
    private String newEmail;

}
