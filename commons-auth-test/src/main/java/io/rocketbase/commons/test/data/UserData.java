package io.rocketbase.commons.test.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.address.Gender;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.user.OnlineProfile;
import io.rocketbase.commons.model.user.PhoneNumber;
import io.rocketbase.commons.model.user.SimpleUserProfile;
import io.rocketbase.commons.model.user.SimpleUserSetting;
import io.rocketbase.commons.test.model.SimpleAppUserEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public abstract class UserData {

    public static SimpleAppUserEntity MARTEN = SimpleAppUserEntity.builder()
            .id("401fb225-057e-4e0a-a0ff-e99e76030d52")
            .username("marten")
            .profile(SimpleUserProfile.builder()
                    .firstName("Marten")
                    .lastName("Prieß")
                    .salutation("Mr.")
                    .gender(Gender.MALE)
                    .location("Winsen (Luhe)")
                    .phoneNumbers(Sets.newHashSet(new PhoneNumber("mobile", "+491512 333 6263")))
                    .onlineProfiles(Sets.newHashSet(new OnlineProfile("xing", "https://www.xing.com/profile/Marten_Priess"),
                            new OnlineProfile("github", "melistik")))
                    .build())
            .setting(SimpleUserSetting.builder()
                    .currentTimeZone("Europe/Berlin")
                    .dateTimeFormat("dd.MM.yyyy HH:mm")
                    .timeFormat("HH:mm")
                    .dateFormat("dd.MM.yyyy")
                    .locale("de")
                    .build())
            .email("marten@rocketbase.io")
            .capabilityIds(Sets.newHashSet(CapabilityData.API_ROOT.getId(), CapabilityData.USER_OBJECT.getId()))
            .password("password")
            .enabled(true)
            .lastLogin(Instant.ofEpochSecond(1612915200)) // 2020-02-10
            .lastTokenInvalidation(Instant.ofEpochSecond(1612310400)) // 2020-02-03
            .created(Instant.ofEpochSecond(1612137600)) // 2020-02-01
            .modified(Instant.ofEpochSecond(1612137600))
            .modifiedBy("test")
            .keyValues(ImmutableMap.of("workspace", "1"))
            .activeTeamId(TeamData.TEAM_IDS.get(0))
            .build();

    public static SimpleAppUserEntity NIELS = SimpleAppUserEntity.builder()
            .id("c3c58d60-e948-442f-9783-c0341c65a367")
            .username("niels")
            .profile(SimpleUserProfile.builder()
                    .firstName("Niels")
                    .lastName("Schelbach")
                    .gender(Gender.MALE)
                    .onlineProfiles(Sets.newHashSet(new OnlineProfile("dockerhub", "rocketbaseio")))
                    .build())
            .setting(SimpleUserSetting.builder()
                    .currentTimeZone("Europe/Berlin")
                    .dateTimeFormat("dd.MM.yyyy HH:mm:ss")
                    .timeFormat("HH:mm:ss")
                    .dateFormat("dd.MM.yyyy")
                    .locale("DE_de")
                    .build())
            .email("niels@rocketbase.io")
            .capabilityIds(Sets.newHashSet(CapabilityData.ROOT.getId()))
            .groupIds(Sets.newHashSet(GroupData.DEPARTMENT_TWO_GROUP.getId()))
            .password("password")
            .enabled(true)
            .activeTeamId(TeamData.TEAM_IDS.get(1))
            .created(Instant.ofEpochSecond(1613088000)) // 2020-02-12
            .modified(Instant.ofEpochSecond(1613088000))
            .modifiedBy("test")
            .keyValues(ImmutableMap.<String, String>builder()
                    .put("workspace", "1")
                    .build())
            .build();

    public static SimpleAppUserEntity LUISE = SimpleAppUserEntity.builder()
            .id("a35dc8fc-57f1-4867-b8a5-04926bc0e91f")
            .username("luise")
            .profile(SimpleUserProfile.builder()
                    .location("Hamburg")
                    .salutation("Mrs")
                    .gender(Gender.MALE)
                    .build())
            .setting(SimpleUserSetting.builder()
                    .currentTimeZone("Europe/Paris")
                    .dateTimeFormat("yyyy-MM-dd HH:mm")
                    .timeFormat("HH:mm")
                    .dateFormat("yyyy-MM-dd")
                    .locale("fr")
                    .build())
            .email("luise@example.com")
            .capabilityIds(Sets.newHashSet(CapabilityData.USER_OBJECT.getId()))
            .groupIds(Sets.newHashSet(GroupData.DEPARTMENT_GROUP.getId(), GroupData.DEPARTMENT_ONE_GROUP.getId()))
            .password("password")
            .enabled(true)
            .created(Instant.ofEpochSecond(1610323200)) // 2020-01-11
            .modified(Instant.ofEpochSecond(1610323200))
            .modifiedBy("test")
            .keyValues(ImmutableMap.<String, String>builder()
                    .put("workspace", "6")
                    .build())
            .build();

    public static SimpleAppUserEntity SAMPLE_DISABLED = SimpleAppUserEntity.builder()
            .id("d74678ea-6689-4c6f-a055-e275b4a2a61c")
            .username("user")
            .profile(SimpleUserProfile.builder()
                    .firstName("Sample")
                    .lastName("User")
                    .build())
            .email("user@rocketbase.io")
            .capabilityIds(Sets.newHashSet(CapabilityData.USER_READ.getId(), CapabilityData.API_BLOG_CRUD.getId()))
            .groupIds(Sets.newHashSet(GroupData.DEPARTMENT_TWO_GROUP.getId()))
            .password("password")
            .enabled(false)
            .activeTeamId(TeamData.TEAM_IDS.get(2))
            .created(Instant.ofEpochSecond(1614211200)) // 2020-02-25
            .modified(Instant.ofEpochSecond(1609459200))
            .modifiedBy("test")
            .build();

    public static SimpleAppUserEntity SERVICE = SimpleAppUserEntity.builder()
            .id("f55e3176-3fca-4100-bb26-853106269fb1")
            .username("service")
            .profile(SimpleUserProfile.builder()
                    .firstName("Service")
                    .build())
            .email("servicee@rocketbase.io")
            .capabilityIds(Sets.newHashSet(CapabilityData.USER_READ.getId(), CapabilityData.API_ASSET.getId(), CapabilityData.API_BLOG_CRUD.getId()))
            .groupIds(Sets.newHashSet(GroupData.DEPARTMENT_TWO_GROUP.getId()))
            .password("password")
            .enabled(true)
            .created(Instant.ofEpochSecond(1609459200)) // 2020-01-01
            .modified(Instant.ofEpochSecond(1609459200))
            .modifiedBy("test")
            .build();

    public static SimpleAppUserEntity ADMIN = SimpleAppUserEntity.builder()
            .id("66253375-273d-46f9-8c1d-37fab682c92b")
            .username("admin")
            .setting(SimpleUserSetting.builder()
                    .currentTimeZone("Europe/London")
                    .dateTimeFormat("yyyy-MM-dd hh:mm a")
                    .timeFormat("hh:mm a")
                    .dateFormat("yyyy-MM-dd")
                    .locale("en")
                    .build())
            .email("admin@rocketbase.io")
            .groupIds(Sets.newHashSet(GroupData.ADMIN_GROUP.getId()))
            .keyValues(ImmutableMap.of("workspace", "1", "displayMode", "short"))
            .password("password")
            .enabled(true)
            .created(Instant.ofEpochSecond(1609459200)) // 2020-01-01
            .modified(Instant.ofEpochSecond(1609459200))
            .modifiedBy("test")
            .build();

    public static List<AppUserEntity> getEntities() {
        return Arrays.asList(MARTEN, NIELS, LUISE, SAMPLE_DISABLED, SERVICE, ADMIN);
    }
}
