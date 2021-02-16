package io.rocketbase.commons.dto.appteam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppTeamShort implements Serializable {

    private Long id;

    private String name;

    public AppTeamShort(AppTeamShort other) {
        this.id = other.id;
        this.name = other.name;
    }
}
