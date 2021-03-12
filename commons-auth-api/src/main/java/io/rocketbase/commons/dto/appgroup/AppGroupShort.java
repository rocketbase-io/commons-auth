package io.rocketbase.commons.dto.appgroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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
     * example: buyers/department/manager
     */
    private String namePath;

    /**
     * calculated parent count
     * <ul>
     *     <li>parent0.parent1.key has depth 2</li>
     *     <li>key has depth 0</li>
     * </ul>
     */
    public int getDepth() {
        return StringUtils.countOccurrencesOf(getNamePath(), "/");
    }

    public AppGroupShort(AppGroupShort other) {
        this.id = other.id;
        this.namePath = other.namePath;
    }
}
