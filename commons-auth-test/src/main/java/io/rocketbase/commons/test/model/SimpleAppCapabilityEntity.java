package io.rocketbase.commons.test.model;

import io.rocketbase.commons.model.AppCapabilityEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAppCapabilityEntity implements AppCapabilityEntity {

    private Long id;

    private String key;

    private String description;

    private Long parentId;

    private String keyPath;

    private boolean withChildren;

    private Instant created;


}


