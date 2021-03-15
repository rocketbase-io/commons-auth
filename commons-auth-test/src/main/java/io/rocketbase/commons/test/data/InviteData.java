package io.rocketbase.commons.test.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.rocketbase.commons.dto.appteam.AppTeamInvite;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.test.model.SimpleAppInviteEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class InviteData {


    public static SimpleAppInviteEntity INVITE_ONE = SimpleAppInviteEntity.builder()
            .id(380059198755045601L)
            .invitor("Max Mustermann")
            .message("A longer Message to the invitor")
            .firstName("Lisa")
            .email("valid@rocketbase.io")
            .capabilityIds(ImmutableSet.of(CapabilityData.ROOT.getId()))
            .keyValues(ImmutableMap.of("workspace", "1",
                    "special", "abc",
                    "_secret", "secure"
            ))
            .groupIds(ImmutableSet.of(GroupData.DEPARTMENT_ONE_GROUP.getId()))
            .expiration(Instant.now().plusSeconds(60 * 60 * 24 * 14)) // plus 14 days
            .created(Instant.now()) // now
            .build();

    public static SimpleAppInviteEntity INVITE_TWO = SimpleAppInviteEntity.builder()
            .id(380059198755045602L)
            .invitor("Marten")
            .message("A longer Message to the invitor")
            .lastName("Müller")
            .email("hello@rocketbase.io")
            .capabilityIds(ImmutableSet.of(CapabilityData.API_BLOG_CRUD.getId(), CapabilityData.USER_READ.getId()))
            .keyValues(ImmutableMap.of("workspace", "2"))
            .groupIds(ImmutableSet.of(GroupData.DEPARTMENT_ONE_GROUP.getId()))
            .expiration(Instant.now().plusSeconds(60 * 60 * 24 * 14)) // plus 14 days
            .created(Instant.now()) // now
            .teamInvite(new AppTeamInvite(TeamData.TEAM_TWO.getId(), AppTeamRole.MEMBER))
            .build();

    public static SimpleAppInviteEntity INVITE_EXPIRED = SimpleAppInviteEntity.builder()
            .id(380059198755045603L)
            .invitor("Admin")
            .message("A longer Message to the invitor")
            .email("expired@rocketbase.io")
            .capabilityIds(ImmutableSet.of(CapabilityData.API_ROOT.getId()))
            .keyValues(ImmutableMap.of("workspace", "1"))
            .groupIds(ImmutableSet.of(GroupData.ADMIN_GROUP.getId()))
            .expiration(Instant.ofEpochSecond(1613088000)) // 2020-02-12
            .created(Instant.ofEpochSecond(1612137600)) // 2020-02-01
            .teamInvite(new AppTeamInvite(TeamData.TEAM_ONE.getId(), AppTeamRole.OWNER))
            .build();

    public static List<AppInviteEntity> getEntities() {
        return Arrays.asList(INVITE_ONE, INVITE_TWO, INVITE_EXPIRED);
    }
}
