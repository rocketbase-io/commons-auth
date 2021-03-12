package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.model.AppClientJpaEntity;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AppClientJpaConverter implements AppClientConverter<AppClientJpaEntity> {

    private final AppCapabilityConverter appCapabilityConverter;

    @Override
    public AppClientRead fromEntity(AppClientJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return AppClientRead.builder()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .name(entity.getName())
                .description(entity.getDescription())
                .capabilities(entity.getCapabilities() != null ? appCapabilityConverter.fromEntities(entity.getCapabilities()).stream().map(AppCapabilityRead::toShort).collect(Collectors.toSet()) : null)
                .redirectUrls(entity.getRedirectUrls())
                .created(entity.getCreated())
                .build();
    }
}
