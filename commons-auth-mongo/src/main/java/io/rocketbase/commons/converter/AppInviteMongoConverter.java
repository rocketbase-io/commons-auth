package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.model.AppInviteMongoEntity;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.group.AppGroupService;
import lombok.RequiredArgsConstructor;

import static io.rocketbase.commons.converter.KeyValueConverter.filterInvisibleKeys;

@RequiredArgsConstructor
public class AppInviteMongoConverter implements AppInviteConverter<AppInviteMongoEntity> {

    private final AppCapabilityService appCapabilityService;
    private final AppGroupService appGroupService;

    @Override
    public AppInviteRead fromEntity(AppInviteMongoEntity entity) {
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
                .capabilities(appCapabilityService.lookupIdsShort(entity.getCapabilityIds()))
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                .groups(appGroupService.lookupIdsShort(entity.getGroupIds()))
                .created(entity.getCreated())
                .expiration(entity.getExpiration())
                .build();
    }
}
