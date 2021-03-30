package io.rocketbase.commons.test.data;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.model.AppTeamEntity;
import io.rocketbase.commons.test.model.SimpleAppTeamEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public abstract class TeamData {

    // solve recursion dep to UserData
    public static List<Long> TEAM_IDS = Arrays.asList(38005872272616450L, 38005872272616451L, 38005872272616452L);

    public static SimpleAppTeamEntity TEAM_ONE = SimpleAppTeamEntity.builder()
            .id(TEAM_IDS.get(0))
            .name("one")
            .description("the one and only")
            .personal(true)
            .keyValues(ImmutableMap.of("location", "de"))
            .members(ImmutableMap.of(UserData.MARTEN.getId(), AppTeamRole.OWNER, UserData.SAMPLE_DISABLED.getId(), AppTeamRole.MEMBER))
            .created(Instant.ofEpochSecond(1609459200)) // 2021-01-01
            .modified(Instant.ofEpochSecond(1609459200))
            .modifiedBy("test")
            .build();

    public static SimpleAppTeamEntity TEAM_TWO = SimpleAppTeamEntity.builder()
            .id(TEAM_IDS.get(1))
            .name("two")
            .description("two is the one!")
            .personal(false)
            .keyValues(ImmutableMap.of("clientId", "123", "emailAlert", "1h"))
            .members(ImmutableMap.of(UserData.NIELS.getId(), AppTeamRole.OWNER, UserData.MARTEN.getId(), AppTeamRole.OWNER, UserData.USER.getId(), AppTeamRole.MEMBER))
            .created(Instant.ofEpochSecond(1609632000)) // 2021-01-03
            .modified(Instant.ofEpochSecond(1609632000))
            .modifiedBy("test")
            .build();

    public static SimpleAppTeamEntity TEAM_THREE = SimpleAppTeamEntity.builder()
            .id(TEAM_IDS.get(2))
            .name("three")
            .description("three my favorite number ;)")
            .personal(false)
            .keyValues(ImmutableMap.of("clientId", "333"))
            .created(Instant.ofEpochSecond(1612224000)) // 2021-02-02
            .modified(Instant.ofEpochSecond(1612224000))
            .modifiedBy("test")
            .build();


    public static List<AppTeamEntity> getEntities() {
        return Arrays.asList(TEAM_ONE, TEAM_TWO, TEAM_THREE);
    }
}
