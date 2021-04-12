package io.rocketbase.commons.vaadin;

import io.rocketbase.commons.model.*;
import io.rocketbase.commons.test.data.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.Collection;

@Configuration
public class DataInitialize implements InitializingBean {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
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
