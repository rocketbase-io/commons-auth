package io.rocketbase.commons.dto.appteam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppTeamShort implements Serializable {

    private Long id;

    private String name;

    public AppTeamShort(AppTeamShort other) {
        this.id = other.id;
        this.name = other.name;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppTeamShort)) return false;
        final AppTeamShort other = (AppTeamShort) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public AppTeamShort(Long id) {
        this.id = id;
    }
}
