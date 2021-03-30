package io.rocketbase.commons.test.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.model.AppGroupEntity;
import io.rocketbase.commons.test.model.SimpleAppGroupEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public abstract class GroupData {

    public static SimpleAppGroupEntity ROOT = SimpleAppGroupEntity.builder()
            .id(AppGroupRead.ROOT.getId())
            .name(AppGroupRead.ROOT.getName())
            .namePath(AppGroupRead.ROOT.getNamePath())
            .systemRefId(AppGroupRead.ROOT.getSystemRefId())
            .description(AppGroupRead.ROOT.getDescription())
            .parentId(AppGroupRead.ROOT.getParentId())
            .withChildren(AppGroupRead.ROOT.isWithChildren())
            .created(AppGroupRead.ROOT.getCreated())
            .modified(AppGroupRead.ROOT.getModified())
            .modifiedBy(AppGroupRead.ROOT.getModifiedBy())
            .keyValues(ImmutableMap.of("displayMode", "normal"))
            .build();

    public static SimpleAppGroupEntity DEPARTMENT_GROUP = SimpleAppGroupEntity.builder()
            .id(38005819381509120L)
            .name("department")
            .namePath("/department")
            .description("department member")
            .parentId(ROOT.getId())
            .keyValues(ImmutableMap.of("displayMode", "detailed", "emailAlert", "1h"))
            .withChildren(true)
            .created(Instant.ofEpochSecond(1609459200)) // 2021-01-01
            .modified(Instant.ofEpochSecond(1609459200))
            .modifiedBy("test")
            .build();

    public static SimpleAppGroupEntity DEPARTMENT_ONE_GROUP = SimpleAppGroupEntity.builder()
            .id(38005819381509121L)
            .name("one")
            .namePath("/department/one")
            .description("department one member")
            .parentId(DEPARTMENT_GROUP.getId())
            .keyValues(ImmutableMap.of("workspace", "66", "displayMode", "short"))
            .withChildren(false)
            .created(Instant.ofEpochSecond(1609459200)) // 2021-01-01
            .modified(Instant.ofEpochSecond(1609459200))
            .modifiedBy("test")
            .build();

    public static SimpleAppGroupEntity DEPARTMENT_TWO_GROUP = SimpleAppGroupEntity.builder()
            .id(38005819381509122L)
            .name("two")
            .namePath("/department/two")
            .description("department two member")
            .parentId(DEPARTMENT_GROUP.getId())
            .keyValues(ImmutableMap.of("emailAlert", "8h"))
            .withChildren(false)
            .created(Instant.ofEpochSecond(1609459200)) // 2021-01-01
            .modified(Instant.ofEpochSecond(1609459200))
            .modifiedBy("test")
            .build();

    public static SimpleAppGroupEntity ADMIN_GROUP = SimpleAppGroupEntity.builder()
            .id(38005819381509123L)
            .name("admin")
            .namePath("/admin")
            .description("group of superuser")
            .parentId(ROOT.getId())
            .withChildren(false)
            .created(Instant.ofEpochSecond(1612137600)) // 2021-02-01
            .modified(Instant.ofEpochSecond(1612137600))
            .capabilityIds(Sets.newHashSet(CapabilityData.ROOT.getId()))
            .modifiedBy("test")
            .keyValues(ImmutableMap.of("displayMode", "detailed"))
            .build();


    public static List<AppGroupEntity> getEntities() {
        return Arrays.asList(ROOT, DEPARTMENT_GROUP, DEPARTMENT_ONE_GROUP, DEPARTMENT_TWO_GROUP, ADMIN_GROUP);
    }
}
