package io.rocketbase.commons.dto.appteam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppTeamMember implements Serializable {

    @NotNull
    private String userId;

    @NotNull
    private AppTeamRole role;
}
