package io.rocketbase.commons.model.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineProfile implements Serializable {

    /**
     * for example: website, linkedIn, pinterest, github, microsoftTeams, slack
     */
    @NotNull
    @Size(max = 15)
    private String type;

    @NotNull
    @Size(max = 255)
    private String value;
}