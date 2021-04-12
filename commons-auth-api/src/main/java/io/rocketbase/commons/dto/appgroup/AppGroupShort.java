package io.rocketbase.commons.dto.appgroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * short version of AppGroup in oder to link it in response of user + invites
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppGroupShort implements Serializable {


    public static AppGroupShort ROOT = new AppGroupShort(6291457024L, null);

    private Long id;

    /**
     * calculated tree of parent's names to get also parent names<br>
     * example: /buyers/department/manager
     */
    private String namePath;

    public AppGroupShort(AppGroupShort other) {
        this.id = other.id;
        this.namePath = other.namePath;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppGroupShort)) return false;
        final AppGroupShort other = (AppGroupShort) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public AppGroupShort(Long id) {
        this.id = id;
    }
}
