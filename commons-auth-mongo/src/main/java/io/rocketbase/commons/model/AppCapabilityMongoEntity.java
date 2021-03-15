package io.rocketbase.commons.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Document(collection = "co_capacity")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
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

    @CreatedDate
    private Instant created;

    public AppCapabilityMongoEntity(Long id) {
        this.id = id;
    }

}
