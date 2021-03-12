package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.model.AppGroupJpaEntity;
import io.rocketbase.commons.model.AppInviteJpaEntity;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import static io.rocketbase.commons.converter.KeyValueConverter.filterInvisibleKeys;

@RequiredArgsConstructor
public class AppInviteJpaConverter implements AppInviteConverter<AppInviteJpaEntity> {

    private final AppCapabilityConverter appCapabilityConverter;
    private final AppGroupConverter<AppGroupJpaEntity> appGroupConverter;

    @Override
    public AppInviteRead fromEntity(AppInviteJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return AppInviteRead.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .invitor(entity.getInvitor())
                .message(entity.getMessage())
                .email(entity.getEmail())
                .teamInvite(entity.getTeamInvite())
                .capabilities(entity.getCapabilities() != null ? appCapabilityConverter.fromEntities(entity.getCapabilities()).stream().map(AppCapabilityRead::toShort).collect(Collectors.toSet()) : null)
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                .groups(entity.getGroups() != null ? appGroupConverter.fromEntities(entity.getGroups()).stream().map(AppGroupRead::toShort).collect(Collectors.toSet()) : null)
                .created(entity.getCreated())
                .expiration(entity.getExpiration())
                .build();
    }
}
