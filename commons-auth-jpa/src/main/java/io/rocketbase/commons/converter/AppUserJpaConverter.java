package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.dto.appteam.AppUserMembership;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.model.AppUserToken;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AppUserJpaConverter implements AppUserConverter<AppUserJpaEntity> {

    private final AppGroupConverter appGroupConverter;
    private final AppCapabilityConverter appCapabilityConverter;
    private final AppTeamConverter appTeamConverter;

    @Override
    public AppUserToken toToken(AppUserJpaEntity entity) {
        return null;
    }

    @Override
    public AppUserRead fromEntity(AppUserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        // TODO: Membership needs to lookup
        return AppUserRead.builder()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .capabilities(entity.getCapabilities() != null ? appCapabilityConverter.fromEntities(entity.getCapabilities()).stream().map(AppCapabilityRead::toShort).collect(Collectors.toSet()) : null)
                .groups(entity.getGroups() != null ? appGroupConverter.fromEntities(entity.getGroups()).stream().map(AppGroupRead::toShort).collect(Collectors.toSet()) : null)
                .activeTeam(entity.getActiveTeam() != null ? new AppUserMembership(appTeamConverter.fromEntity(entity.getActiveTeam()).toShort(), AppTeamRole.MEMBER) : null)
                .keyValues(KeyValueConverter.filterInvisibleKeys(entity.getKeyValues()))
                .enabled(entity.isEnabled())
                .locked(entity.isLocked())
                .created(entity.getCreated())
                .lastLogin(entity.getLastLogin())
                .profile(entity.getProfile())
                .setting(entity.getSetting())
                .build();
    }

}
