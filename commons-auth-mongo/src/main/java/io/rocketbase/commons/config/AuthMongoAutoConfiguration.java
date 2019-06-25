package io.rocketbase.commons.config;

import io.rocketbase.commons.model.AppUserMongoEntity;
import io.rocketbase.commons.service.AppUserMongoServiceImpl;
import io.rocketbase.commons.service.AppUserPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class AuthMongoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AppUserPersistenceService<AppUserMongoEntity> appUserPersistenceService(@Autowired MongoTemplate mongoTemplate) {
        return new AppUserMongoServiceImpl(mongoTemplate);
    }
}
