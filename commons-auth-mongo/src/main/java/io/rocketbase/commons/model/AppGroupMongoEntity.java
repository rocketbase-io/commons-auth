package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Document(collection = "${auth.entity.prefix:co_}group")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppGroupMongoEntity implements AppGroupEntity {

    @Id
    private Long id;

    @Nullable
    private String systemRefId;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private Long parentId;

    @NotNull
    private String namePath;

    private boolean withChildren;

    private Set<Long> capabilityIds;

    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

    @NotNull
    @CreatedDate
    private Instant created;

    @LastModifiedBy
    private String modifiedBy;

    @NotNull
    @LastModifiedDate
    private Instant modified;

    public AppGroupMongoEntity(Long id) {
        this.id = id;
    }

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
