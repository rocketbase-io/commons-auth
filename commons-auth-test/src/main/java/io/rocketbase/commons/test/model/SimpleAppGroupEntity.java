package io.rocketbase.commons.test.model;

import io.rocketbase.commons.model.AppGroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAppGroupEntity implements AppGroupEntity {

    private Long id;

    private String name;

    private String systemRefId;

    private String description;

    private Set<Long> capabilityIds;

    private Map<String, String> keyValues = new HashMap<>();

    private Long parentId;

    private String namePath;

    private boolean withChildren;

    private Instant created;

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppGroupEntity)) return false;
        final AppGroupEntity other = (AppGroupEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}


