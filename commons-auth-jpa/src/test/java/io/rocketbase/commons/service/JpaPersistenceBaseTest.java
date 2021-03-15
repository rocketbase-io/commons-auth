package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.model.*;
import io.rocketbase.commons.test.data.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collection;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class JpaPersistenceBaseTest {

    @Resource
    private EntityManager em;

    @Before
    public void initDemoData() {
        truncateAndSave(CapabilityData.getEntities(), AppCapabilityJpaEntity.class);
        truncateAndSave(ClientData.getEntities(), AppClientJpaEntity.class);
        truncateAndSave(GroupData.getEntities(), AppGroupJpaEntity.class);
        truncateAndSave(TeamData.getEntities(), AppTeamJpaEntity.class);
        truncateAndSave(InviteData.getEntities(), AppInviteJpaEntity.class);
        truncateAndSave(UserData.getEntities(), AppUserJpaEntity.class);
    }

    protected void truncateAndSave(Collection collection, Class clazz) {
        SimpleJpaRepository repository = new SimpleJpaRepository<>(clazz, em);
        repository.deleteAll();
        repository.saveAll(collection);
    }

}
