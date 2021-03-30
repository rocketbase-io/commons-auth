package io.rocketbase.commons.dto.appcapability;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * short version of AppCapability in oder to link it in response of user + invites
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppCapabilityShort implements Serializable {

    public static AppCapabilityShort ROOT = new AppCapabilityShort(6291457024L, null);

    private Long id;

    /**
     * calculated path<br>
     * concat each parent0.parent1.key split by .
     */
    private String keyPath;
}
