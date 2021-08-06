package io.rocketbase.commons.dto.appgroup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rocketbase.commons.dto.appcapability.AppCapabilityShort;
import io.rocketbase.commons.model.HasKeyValue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppGroupRead extends AppGroupShort implements HasKeyValue {

    public static AppGroupRead ROOT = new AppGroupRead(6291457024L, "/", null, "*",
            "root group", true, 6291457024L, null, null, Instant.ofEpochMilli(1577836800000L), "commons-auth", Instant.ofEpochMilli(1577836800000L));

    @Nullable
    private String systemRefId;

    private String name;

    private String description;

    /**
     * is group parent for any other group in the database
     */
    private boolean withChildren;

    private Long parentId;

    private Set<AppCapabilityShort> capabilities;

    @Nullable
    private Map<String, String> keyValues;

    private Instant created;

    private String modifiedBy;

    private Instant modified;

    @JsonIgnore
    public AppGroupShort toShort() {
        return new AppGroupShort(
                getId(),
                getNamePath()
        );
    }

    @Builder
    public AppGroupRead(Long id, String namePath, String systemRefId, String name, String description, boolean withChildren, Long parentId, Set<AppCapabilityShort> capabilities, Map<String, String> keyValues, Instant created, String modifiedBy,  Instant modified) {
        super(id, namePath);
        this.systemRefId = systemRefId;
        this.name = name;
        this.description = description;
        this.withChildren = withChildren;
        this.parentId = parentId;
        this.capabilities = capabilities;
        this.keyValues = keyValues;
        this.created = created;
        this.modifiedBy = modifiedBy;
        this.modified = modified;
    }

    public AppGroupRead(Long id) {
        super(id);
    }
}
