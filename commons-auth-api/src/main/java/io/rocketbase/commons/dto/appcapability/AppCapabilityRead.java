package io.rocketbase.commons.dto.appcapability;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppCapabilityRead extends AppCapabilityShort implements Serializable {

    public static AppCapabilityRead ROOT = new AppCapabilityRead(6291457024L, "*", "*", true, "root capability", 6291457024L, Instant.ofEpochMilli(1577836800000L), "commons-auth", Instant.ofEpochMilli(1577836800000L));

    private String key;

    /**
     * is capability parent for any other capability in the database
     */
    private boolean withChildren;

    private String description;

    private Long parentId;

    private Instant created;

    private String modifiedBy;

    private Instant modified;

    @JsonIgnore
    public AppCapabilityShort toShort() {
        return new AppCapabilityShort(
                getId(),
                getKeyPath()
        );
    }

    @Builder
    public AppCapabilityRead(Long id, String keyPath, String key, boolean withChildren, String description, Long parentId, Instant created, String modifiedBy,  Instant modified) {
        super(id, keyPath);
        this.key = key;
        this.withChildren = withChildren;
        this.description = description;
        this.parentId = parentId;
        this.created = created;
        this.modifiedBy = modifiedBy;
        this.modified = modified;
    }
}
