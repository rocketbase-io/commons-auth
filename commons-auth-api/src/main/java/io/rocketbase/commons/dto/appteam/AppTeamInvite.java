package io.rocketbase.commons.dto.appteam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * used in case of user/invite creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppTeamInvite implements Serializable {

    @NotNull
    private Long teamId;

    @NotNull
    private AppTeamRole role;
}
