package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.dto.appteam.AppUserMembership;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.*;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.group.AppGroupService;
import io.rocketbase.commons.service.team.AppTeamService;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.rocketbase.commons.converter.KeyValueConverter.filterInvisibleAndJwtIgnoredKeys;
import static io.rocketbase.commons.converter.KeyValueConverter.filterInvisibleKeys;

@RequiredArgsConstructor
public class AppUserJpaConverter implements AppUserConverter<AppUserJpaEntity> {

    private final AppGroupConverter appGroupConverter;
    private final AppGroupService appGroupService;
    private final AppCapabilityConverter appCapabilityConverter;
    private final AppCapabilityService appCapabilityService;
    private final AppTeamService appTeamService;
    private final AppTeamConverter appTeamConverter;

    @Override
    public AppUserToken toToken(AppUserJpaEntity entity) {
        // load groups and
        Set<AppGroupEntity> groupEntities = appGroupService.followTreeUpwards(entity.getGroupIds());
        // resolve capabilities
        Set<Long> capabilityIds = Nulls.notNull(entity.getCapabilityIds());
        capabilityIds.addAll(getCapabilityIdsOfGroups(groupEntities));

        // load activeTeam
        Pair<AppTeamEntity, AppTeamRole> teamRolePair = appTeamService.lookupMembership(entity.getActiveTeamId(), entity.getId());

        return SimpleAppUserToken.builderToken()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .profile(entity.getProfile())
                .groups(convertGroups(groupEntities))
                .capabilities(appCapabilityService.resolve(capabilityIds))
                .activeTeam(convertActiveTeam(teamRolePair))
                .keyValues(filterInvisibleAndJwtIgnoredKeys(resolveKeyValues(groupEntities, teamRolePair != null ? teamRolePair.getFirst() : null, entity)))
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
                .groups(convertGroups(entity.getGroups()))
                .activeTeam(convertActiveTeam(appTeamService.lookupMembership(entity.getActiveTeamId(), entity.getId())))
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                .enabled(entity.isEnabled())
                .locked(entity.isLocked())
                .created(entity.getCreated())
                .lastLogin(entity.getLastLogin())
                .profile(entity.getProfile())
                .setting(entity.getSetting())
                .build();
    }

    protected AppUserMembership convertActiveTeam(Pair<AppTeamEntity, AppTeamRole> teamRolePair) {
        return teamRolePair != null ? new AppUserMembership(appTeamConverter.fromEntity(teamRolePair.getFirst()).toShort(), teamRolePair.getSecond()) : null;
    }

    protected Set<AppGroupShort> convertGroups(Collection<? extends AppGroupEntity> groups) {
        if (groups == null) {
            return null;
        }
        List<AppGroupRead> converted = appGroupConverter.fromEntities(groups);
        return converted.stream().map(AppGroupRead::toShort).collect(Collectors.toSet());
    }

}