package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.model.*;
import io.rocketbase.commons.test.data.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collection;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MongoPersistenceBaseTest {

    @Resource
    private MongoTemplate mongoTemplate;

    @Before
    public void initDemoData() {
        truncateAndSave(CapabilityData.getEntities(), AppCapabilityMongoEntity.COLLECTION_NAME);
        truncateAndSave(ClientData.getEntities(), AppClientMongoEntity.COLLECTION_NAME);
        truncateAndSave(GroupData.getEntities(), AppGroupMongoEntity.COLLECTION_NAME);
        truncateAndSave(TeamData.getEntities(), AppTeamMongoEntity.COLLECTION_NAME);
        truncateAndSave(InviteData.getEntities(), AppInviteMongoEntity.COLLECTION_NAME);
        truncateAndSave(UserData.getEntities(), AppUserMongoEntity.COLLECTION_NAME);
    }

    protected void truncateAndSave(Collection collection, String collectionName) {
        mongoTemplate.remove(new Query(), collectionName);
        for (Object o : collection) {
            mongoTemplate.save(o, collectionName);
        }
    }

}
