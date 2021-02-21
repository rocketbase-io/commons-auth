package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.model.AppCapabilityEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AppCapabilityConverter {

    public <C extends AppCapabilityEntity> AppCapabilityRead fromEntity(C entity) {
        if (entity == null) {
            return null;
        }
        return AppCapabilityRead.builder()
                .id(entity.getId())
                .keyPath(entity.getKeyPath())
                .key(entity.getKey())
                .withChildren(entity.isWithChildren())
                .description(entity.getDescription())
                .parentId(entity.getParentId())
                .build();
    }

    public <C extends AppCapabilityEntity> List<AppCapabilityRead> fromEntities(Collection<C> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }
}
