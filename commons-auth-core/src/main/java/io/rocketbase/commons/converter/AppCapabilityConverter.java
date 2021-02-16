package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.model.AppCapabilityEntity;

import java.util.List;
import java.util.stream.Collectors;

public class AppCapabilityConverter {

    public AppCapabilityRead toRead(AppCapabilityEntity entity) {
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

    public List<AppCapabilityRead> toRead(List<AppCapabilityEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> toRead(e))
                .collect(Collectors.toList());
    }
}
