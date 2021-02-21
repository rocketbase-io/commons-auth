package io.rocketbase.commons.model;

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
import java.util.Set;

@Document(collection = "co_group")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AppGroupMongoEntity implements AppGroupEntity {

    @Id
    private Long id;

    @Nullable
    @Indexed
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

    @CreatedDate
    private Instant created;

    public AppGroupMongoEntity(Long id) {
        this.id = id;
    }

}
