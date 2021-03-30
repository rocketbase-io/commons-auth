package io.rocketbase.commons.dto.appgroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * short version of AppGroup in oder to link it in response of user + invites
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
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
}
