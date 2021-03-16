package io.rocketbase.commons.test.model;

import io.rocketbase.commons.model.AppClientEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAppClientEntity implements AppClientEntity {

    private Long id;

    private String name;

    private String systemRefId;

    private String description;

    private Set<Long> capabilityIds;

    private Set<String> redirectUrls;

    private Instant created;

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppClientEntity)) return false;
        final AppClientEntity other = (AppClientEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
