package io.rocketbase.commons.dto.appinvite;

import io.rocketbase.commons.model.HasFirstAndLastName;
import io.rocketbase.commons.model.HasKeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * database entity representation of invited persons
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Schema(description = "database entity representation of invited persons")
public class AppInviteRead implements HasKeyValue, HasFirstAndLastName {
    private String id;

    @Schema(description = "name that will get displayed to person within email")
    private String invitor;

    @Schema(description = "custom message to the invited person")
    private String message;

    private String firstName;

    private String lastName;

    private String email;

    @Singular
    private List<String> roles;

    @Singular
    private Map<String, String> keyValues;

    private Instant created;

    private Instant expiration;
}
