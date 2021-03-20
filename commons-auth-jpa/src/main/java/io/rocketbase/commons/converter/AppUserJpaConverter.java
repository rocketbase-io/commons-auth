package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppGroupJpaEntity;
import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.SimpleAppUserToken;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.team.AppTeamService;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import static io.rocketbase.commons.converter.KeyValueConverter.filterInvisibleKeys;

@RequiredArgsConstructor
public class AppUserJpaConverter implements AppUserConverter<AppUserJpaEntity> {

    private final AppGroupConverter<AppGroupJpaEntity> appGroupConverter;
    private final AppCapabilityConverter appCapabilityConverter;
    private final AppCapabilityService appCapabilityService;
    private final AppTeamService appTeamService;

    @Override
    public AppUserToken toToken(AppUserJpaEntity entity) {
        return SimpleAppUserToken.builderToken()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .profile(entity.getProfile())
                .groups(entity.getGroups() != null ? appGroupConverter.fromEntities(entity.getGroups()).stream().map(AppGroupRead::toShort).collect(Collectors.toSet()) : null)
                .capabilities(appCapabilityService.resolve(entity.getCapabilityIds()))
                .activeTeam(appTeamService.lookupMembership(entity.getActiveTeamId(), entity.getId()))
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                .setting(entity.getSetting())
                .build();
    }

    @Override
    public AppUserRead fromEntity(AppUserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return AppUserRead.builder()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .capabilities(entity.getCapabilities() != null ? appCapabilityConverter.fromEntities(entity.getCapabilities()).stream().map(AppCapabilityRead::toShort).collect(Collectors.toSet()) : null)
                .groups(entity.getGroups() != null ? appGroupConverter.fromEntities(entity.getGroups()).stream().map(AppGroupRead::toShort).collect(Collectors.toSet()) : null)
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
