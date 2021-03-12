package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUserMongoEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.group.AppGroupService;
import io.rocketbase.commons.service.team.AppTeamService;
import lombok.RequiredArgsConstructor;

import static io.rocketbase.commons.converter.KeyValueConverter.filterInvisibleKeys;

@RequiredArgsConstructor
public class AppUserMongoConverter implements AppUserConverter<AppUserMongoEntity> {

    private final AppCapabilityService appCapabilityService;
    private final AppGroupService appGroupService;
    private final AppTeamService appTeamService;

    @Override
    public AppUserToken toToken(AppUserMongoEntity entity) {
        return null;
    }

    @Override
    public AppUserRead fromEntity(AppUserMongoEntity entity) {
        if (entity == null) {
            return null;
        }
        return AppUserRead.builder()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .capabilities(appCapabilityService.lookupIdsShort(entity.getCapabilityIds()))
                .groups(appGroupService.lookupIdsShort(entity.getGroupIds()))
                .activeTeam(appTeamService.lookupMembership(entity.getActiveTeamId(), entity.getId()))
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                .enabled(entity.isEnabled())
                .locked(entity.isLocked())
                .created(entity.getCreated())
                .lastLogin(entity.getLastLogin())
                .profile(entity.getProfile())
                .setting(entity.getSetting())
                .build();
    }

}
