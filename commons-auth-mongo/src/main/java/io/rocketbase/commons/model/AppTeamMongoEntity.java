package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.appteam.AppTeamRole;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.rocketbase.commons.model.AppTeamMongoEntity.COLLECTION_NAME;


@Document(collection = COLLECTION_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppTeamMongoEntity implements AppTeamEntity {

    public static final String COLLECTION_NAME = "co_team";

    @Id
    private Long id;

    @Nullable
    @Indexed
    private String systemRefId;

    @NotNull
    private String name;

    private String description;

    private boolean personal;

    @NotNull
    @CreatedDate
    private Instant created;

    @LastModifiedBy
    private String modifiedBy;

    @NotNull
    @LastModifiedDate
    private Instant modified;

    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

    @Builder.Default
    private Map<String, AppTeamRole> members = new HashMap<>();

    public AppTeamMongoEntity(Long id) {
        this.id = id;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AppTeamEntity)) return false;
        final AppTeamEntity other = (AppTeamEntity) o;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
