package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.test.data.*;
import io.rocketbase.commons.util.CommonsAuthCollectionNameResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.Collection;

@Slf4j
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MongoPersistenceBaseTest {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private CommonsAuthCollectionNameResolver collectionNameResolver;

    @BeforeEach
    public void initDemoData() {
        truncateAndSave(CapabilityData.getEntities(), collectionNameResolver.capability());
        truncateAndSave(ClientData.getEntities(), collectionNameResolver.client());
        truncateAndSave(GroupData.getEntities(), collectionNameResolver.group());
        truncateAndSave(TeamData.getEntities(), collectionNameResolver.team());
        truncateAndSave(InviteData.getEntities(), collectionNameResolver.invite());
        truncateAndSave(UserData.getEntities(), collectionNameResolver.user());
    }

    protected void truncateAndSave(Collection collection, String collectionName) {
        mongoTemplate.remove(new Query(), collectionName);
        for (Object o : collection) {
            mongoTemplate.save(o, collectionName);
        }
    }

    @Disabled
    @Test
    public void upAndRunning() {
        log.info("running tests with database: {}", mongoTemplate.getDb().getName());
    }

}
