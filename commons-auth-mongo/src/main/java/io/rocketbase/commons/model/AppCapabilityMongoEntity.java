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

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

@Document(collection = "co_capacity")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppCapabilityMongoEntity implements AppCapabilityEntity {

    public static final String COLLECTION_NAME = "co_capacity";

    @Id
    private Long id;

    private String key;

    private String description;

    @NotNull
    private Long parentId;

    @NotNull
    private String keyPath;

    private boolean withChildren;

    @NotNull
    @CreatedDate
    private Instant created;

    @LastModifiedBy
    private String modifiedBy;

    @NotNull
    @LastModifiedDate
    private Instant modified;

    public AppCapabilityMongoEntity(Long id) {
        this.id = id;
    }

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
