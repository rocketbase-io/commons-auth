package io.rocketbase.commons.dto.appteam;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * used in case of user/invite creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppTeamInvite implements Serializable {

    @NotNull
    private Long teamId;

    @NotNull
    private AppTeamRole role;
}
