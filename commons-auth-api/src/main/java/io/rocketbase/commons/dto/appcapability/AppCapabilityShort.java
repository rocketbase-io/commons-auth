package io.rocketbase.commons.dto.appcapability;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * short version of AppCapability in oder to link it in response of user + invites
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppCapabilityShort implements Serializable {

    private Long id;

    /**
     * calculated path<br>
     * concat each parent0.parent1.key split by .
     */
    private String keyPath;

    /**
     * calculated parent count
     * <ul>
     *     <li>parent0.parent1.key has depth 2</li>
     *     <li>key has depth 0</li>
     * </ul>
     */
    public int getDepth() {
        return StringUtils.countOccurrencesOf(getKeyPath(), ".");
    }
}
