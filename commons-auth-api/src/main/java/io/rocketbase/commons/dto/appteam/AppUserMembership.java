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
public class AppUserMembership implements Serializable {

    @NotNull
    private AppTeamShort team;

    @NotNull
    private AppTeamRole role;

    public AppUserMembership(AppUserMembership other) {
        this.team = other.team != null ? new AppTeamShort(other.team) : null;
        this.role = other.role;
    }
}
