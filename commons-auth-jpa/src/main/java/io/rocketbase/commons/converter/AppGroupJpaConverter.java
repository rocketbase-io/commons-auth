package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.model.AppGroupJpaEntity;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AppGroupJpaConverter implements AppGroupConverter<AppGroupJpaEntity> {

    private final AppCapabilityConverter appCapabilityConverter;

    @Override
    public AppGroupRead fromEntity(AppGroupJpaEntity entity) {
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
                .capabilities(entity.getCapabilities() != null ? appCapabilityConverter.fromEntities(entity.getCapabilities()).stream().map(AppCapabilityRead::toShort).collect(Collectors.toSet()) : null)
                .created(entity.getCreated())
                .build();
    }
}
