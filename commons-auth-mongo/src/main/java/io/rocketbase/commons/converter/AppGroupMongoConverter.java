package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.model.AppGroupMongoEntity;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AppGroupMongoConverter implements AppGroupConverter<AppGroupMongoEntity> {

    private final AppCapabilityService appCapabilityService;

    @Override
    public AppGroupRead fromEntity(AppGroupMongoEntity entity) {
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
                .created(entity.getCreated())
                .build();
    }
}
