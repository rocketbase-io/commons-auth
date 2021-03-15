package io.rocketbase.commons.test.model;

import io.rocketbase.commons.model.AppGroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAppGroupEntity implements AppGroupEntity {

    private Long id;

    private String name;

    private String systemRefId;

    private String description;

    private Set<Long> capabilityIds;

    private Map<String, String> keyValues = new HashMap<>();

    private Long parentId;

    private String namePath;

    private boolean withChildren;

    private Instant created;


}


