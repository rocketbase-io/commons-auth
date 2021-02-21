package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.model.AppGroupEntity;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AppGroupConverter {

    private final AppCapabilityService appCapabilityService;

    public <G extends AppGroupEntity> AppGroupRead fromEntity(G entity) {
        if (entity == null) {
            return null;
        }
        return AppGroupRead.builder()
                .id(entity.getId())
                .namePath(entity.getNamePath())
                .systemRefId(entity.getSystemRefId())
                .name(entity.getName())
                .description(entity.getDescription())
                .withChildren(entity.isWithChildren())
                .parentId(entity.getParentId())
                .capabilities(appCapabilityService.lookupIdsShort(entity.getCapabilityIds()))
                .build();
    }

    public <G extends AppGroupEntity> List<AppGroupRead> fromEntities(Collection<G> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> fromEntity(e))
                .collect(Collectors.toList());
    }
}
