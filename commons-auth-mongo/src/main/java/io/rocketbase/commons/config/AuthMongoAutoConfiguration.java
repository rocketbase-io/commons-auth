package io.rocketbase.commons.config;

import io.rocketbase.commons.model.AppInviteMongoEntity;
import io.rocketbase.commons.model.AppUserMongoEntity;
import io.rocketbase.commons.service.AppInviteMongoServiceImpl;
import io.rocketbase.commons.service.AppInvitePersistenceService;
import io.rocketbase.commons.service.AppUserMongoServiceImpl;
import io.rocketbase.commons.service.AppUserPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@AutoConfigureBefore(AuthServiceAutoConfiguration.class)
public class AuthMongoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AppUserPersistenceService<AppUserMongoEntity> appUserPersistenceService(@Autowired MongoTemplate mongoTemplate) {
        return new AppUserMongoServiceImpl(mongoTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppInvitePersistenceService<AppInviteMongoEntity> appInvitePersistenceService(@Autowired MongoTemplate mongoTemplate) {
        return new AppInviteMongoServiceImpl(mongoTemplate);
    }
}
