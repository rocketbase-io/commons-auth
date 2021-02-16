package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.model.AppGroupEntity;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AppGroupConverter {

    private final AppCapabilityService appCapabilityService;

    public AppGroupRead toRead(AppGroupEntity entity) {
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

    public List<AppGroupRead> toRead(List<AppGroupEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(e -> toRead(e))
                .collect(Collectors.toList());
    }
}
