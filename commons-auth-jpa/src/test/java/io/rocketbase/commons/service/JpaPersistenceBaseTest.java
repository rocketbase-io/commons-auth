package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.model.*;
import io.rocketbase.commons.test.data.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;

@Slf4j
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class JpaPersistenceBaseTest {

    @Resource
    private EntityManager em;

    @BeforeEach
    public void initDemoData() throws Exception {
        truncateAndSave(CapabilityData.getEntities(), AppCapabilityJpaEntity.class, (Function<AppCapabilityEntity, AppCapabilityJpaEntity>) data -> {
            AppCapabilityJpaEntity e = new AppCapabilityJpaEntity();
            BeanUtils.copyProperties(data, e);
            e.setParent(new AppCapabilityJpaEntity(data.getParentId()));
            return e;
        });
        truncateAndSave(ClientData.getEntities(), AppClientJpaEntity.class, (Function<AppClientEntity, AppClientJpaEntity>) data -> {
            AppClientJpaEntity e = new AppClientJpaEntity();
            BeanUtils.copyProperties(data, e);
            e.setCapabilities(convertCapabilities(data.getCapabilityIds()));
            return e;
        });
        truncateAndSave(GroupData.getEntities(), AppGroupJpaEntity.class, (Function<AppGroupEntity, AppGroupJpaEntity>) data -> {
            AppGroupJpaEntity e = new AppGroupJpaEntity();
            BeanUtils.copyProperties(data, e);
            e.setParent(new AppGroupJpaEntity(data.getParentId()));
            e.setCapabilities(convertCapabilities(data.getCapabilityIds()));
            return e;
        });
        truncateAndSave(TeamData.getEntities(), AppTeamJpaEntity.class, (Function<AppTeamEntity, AppTeamJpaEntity>) data -> {
            AppTeamJpaEntity e = new AppTeamJpaEntity();
            BeanUtils.copyProperties(data, e);
            return e;
        });
        truncateAndSave(InviteData.getEntities(), AppInviteJpaEntity.class, (Function<AppInviteEntity, AppInviteJpaEntity>) data -> {
            AppInviteJpaEntity e = new AppInviteJpaEntity();
            BeanUtils.copyProperties(data, e);
            e.setCapabilities(convertCapabilities(data.getCapabilityIds()));
            e.setGroups(convertGroups(data.getGroupIds()));
            return e;
        });
        truncateAndSave(UserData.getEntities(), AppUserJpaEntity.class, (Function<AppUserEntity, AppUserJpaEntity>) data -> {
            AppUserJpaEntity e = new AppUserJpaEntity();
            BeanUtils.copyProperties(data, e);
            e.setCapabilities(convertCapabilities(data.getCapabilityIds()));
            e.setGroups(convertGroups(data.getGroupIds()));
            e.setActiveTeam(data.getActiveTeamId() != null ? new AppTeamJpaEntity(data.getActiveTeamId()) : null);
            return e;
        });
    }

    private Set<AppCapabilityJpaEntity> convertCapabilities(Set<Long> ids) {
        Set<AppCapabilityJpaEntity> capabilities = new HashSet<>();
        if (ids != null) {
            for (Long cid : ids) {
                capabilities.add(new AppCapabilityJpaEntity(cid));
            }
        }
        return capabilities;
    }

    private Set<AppGroupJpaEntity> convertGroups(Set<Long> ids) {
        Set<AppGroupJpaEntity> groups = new HashSet<>();
        if (ids != null) {
            for (Long gid : ids) {
                groups.add(new AppGroupJpaEntity(gid));
            }
        }
        return groups;
    }

    protected void truncateAndSave(Collection collection, Class clazz, Function function) throws Exception {
        SimpleJpaRepository repository = new SimpleJpaRepository<>(clazz, em);
        repository.deleteAll();

        List values = new ArrayList<>();
        for (Object o : collection) {
            values.add(function.apply(o));
        }
        repository.saveAll(values);
    }

    @Disabled
    @Test
    public void upAndRunning() {
        log.info("running tests with entities: {}", em.getMetamodel().getEntities());
    }

}
