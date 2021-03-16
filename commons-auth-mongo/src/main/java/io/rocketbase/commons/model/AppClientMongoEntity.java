package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import static io.rocketbase.commons.model.AppClientMongoEntity.COLLECTION_NAME;

@Document(collection = COLLECTION_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppClientMongoEntity implements AppClientEntity {

    public static final String COLLECTION_NAME = "co_client";

    @Id
    private Long id;

    @Nullable
    @Indexed
    private String systemRefId;

    @NotNull
    private String name;

    private String description;

    private Set<Long> capabilityIds;

    private Set<String> redirectUrls;

    @CreatedDate
    private Instant created;

    public AppClientMongoEntity(Long id) {
        this.id = id;
    }

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
