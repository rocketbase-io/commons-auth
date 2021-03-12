package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.model.AppClientMongoEntity;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AppClientMongoConverter implements AppClientConverter<AppClientMongoEntity> {

    private final AppCapabilityService appCapabilityService;

    @Override
    public AppClientRead fromEntity(AppClientMongoEntity entity) {
        if (entity == null) {
            return null;
        }
        return AppClientRead.builder()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .name(entity.getName())
                .description(entity.getDescription())
                .capabilities(appCapabilityService.lookupIdsShort(entity.getCapabilityIds()))
                .redirectUrls(entity.getRedirectUrls())
                .created(entity.getCreated())
                .build();
    }
}
