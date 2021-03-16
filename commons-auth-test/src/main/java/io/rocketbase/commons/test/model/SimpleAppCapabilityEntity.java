package io.rocketbase.commons.test.model;

import io.rocketbase.commons.model.AppCapabilityEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAppCapabilityEntity implements AppCapabilityEntity {

    private Long id;

    private String key;

    private String description;

    private Long parentId;

    private String keyPath;

    private boolean withChildren;

    private Instant created;

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppCapabilityEntity)) return false;
        final AppCapabilityEntity other = (AppCapabilityEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


