package io.rocketbase.commons.dto.appcapability;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * short version of AppCapability in oder to link it in response of user + invites
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppCapabilityShort implements Serializable {

    public static AppCapabilityShort ROOT = new AppCapabilityShort(6291457024L, null);

    private Long id;

    /**
     * calculated path<br>
     * concat each parent0.parent1.key split by .
     */
    private String keyPath;

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppCapabilityShort)) return false;
        final AppCapabilityShort other = (AppCapabilityShort) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public AppCapabilityShort(Long id) {
        this.id = id;
    }
}
