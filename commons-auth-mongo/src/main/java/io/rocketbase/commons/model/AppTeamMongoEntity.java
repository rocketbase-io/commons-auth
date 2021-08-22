package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.appteam.AppTeamRole;
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


@Document(collection = "${auth.entity.prefix:co_}team")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppTeamMongoEntity implements AppTeamEntity {

    @Id
    private Long id;

    @Nullable
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
