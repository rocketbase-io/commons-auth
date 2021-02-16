package io.rocketbase.commons.dto.appcapability;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppCapabilityRead extends AppCapabilityShort implements Serializable {

    private String key;

    /**
     * is capability parent for any other capability in the database
     */
    private boolean withChildren;

    private String description;

    private Long parentId;

    @JsonIgnore
    public AppCapabilityShort toShort() {
        return new AppCapabilityShort(
                getId(),
                getKeyPath()
        );
    }

    @Builder
    public AppCapabilityRead(Long id, String keyPath, String key, boolean withChildren, String description, Long parentId) {
        super(id, keyPath);
        this.key = key;
        this.withChildren = withChildren;
        this.description = description;
        this.parentId = parentId;
    }
}
