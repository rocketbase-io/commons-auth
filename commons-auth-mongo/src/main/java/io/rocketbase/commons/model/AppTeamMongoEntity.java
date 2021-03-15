package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.appteam.AppTeamRole;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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

    @CreatedDate
    private Instant created;

    @Builder.Default
    private Map<String, String> keyValues = new HashMap<>();

    @Builder.Default
    private Map<String, AppTeamRole> members = new HashMap<>();

    public AppTeamMongoEntity(Long id) {
        this.id = id;
    }

}
