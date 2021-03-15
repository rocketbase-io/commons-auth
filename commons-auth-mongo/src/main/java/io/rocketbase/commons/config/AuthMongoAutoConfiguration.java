package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.model.*;
import io.rocketbase.commons.service.capability.AppCapabilityMongoPersistenceService;
import io.rocketbase.commons.service.capability.AppCapabilityPersistenceService;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.client.AppClientMongoPersistenceService;
import io.rocketbase.commons.service.client.AppClientPersistenceService;
import io.rocketbase.commons.service.group.AppGroupMongoPersistenceService;
import io.rocketbase.commons.service.group.AppGroupPersistenceService;
import io.rocketbase.commons.service.group.AppGroupService;
import io.rocketbase.commons.service.invite.AppInviteMongoPersistenceService;
import io.rocketbase.commons.service.invite.AppInvitePersistenceService;
import io.rocketbase.commons.service.team.AppTeamMongoPersistenceService;
import io.rocketbase.commons.service.team.AppTeamPersistenceService;
import io.rocketbase.commons.service.team.AppTeamService;
import io.rocketbase.commons.service.user.AppUserMongoPersistenceService;
import io.rocketbase.commons.service.user.AppUserPersistenceService;
import io.rocketbase.commons.util.Snowflake;
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
        return new AppUserMongoPersistenceService(mongoTemplate, AppUserMongoEntity.COLLECTION_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppInvitePersistenceService<AppInviteMongoEntity> appInvitePersistenceService(@Autowired MongoTemplate mongoTemplate, @Autowired Snowflake snowflake) {
        return new AppInviteMongoPersistenceService(mongoTemplate, snowflake, AppInviteMongoEntity.COLLECTION_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppCapabilityPersistenceService<AppCapabilityMongoEntity> appCapabilityPersistenceService(@Autowired MongoTemplate mongoTemplate, @Autowired Snowflake snowflake) {
        return new AppCapabilityMongoPersistenceService(mongoTemplate, snowflake, AppCapabilityMongoEntity.COLLECTION_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppTeamPersistenceService<AppTeamMongoEntity> appTeamPersistenceService(@Autowired MongoTemplate mongoTemplate, @Autowired Snowflake snowflake) {
        return new AppTeamMongoPersistenceService(mongoTemplate, snowflake, AppTeamMongoEntity.COLLECTION_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppGroupPersistenceService<AppGroupMongoEntity> appGroupPersistenceService(@Autowired MongoTemplate mongoTemplate, @Autowired Snowflake snowflake) {
        return new AppGroupMongoPersistenceService(mongoTemplate, snowflake, AppGroupMongoEntity.COLLECTION_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppClientPersistenceService<AppClientMongoEntity> appClientPersistenceService(@Autowired MongoTemplate mongoTemplate, @Autowired Snowflake snowflake) {
        return new AppClientMongoPersistenceService(mongoTemplate, snowflake, AppClientMongoEntity.COLLECTION_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserConverter<AppUserMongoEntity> appUserConverter(@Autowired AppCapabilityService appCapabilityService, @Autowired AppGroupService appGroupService, @Autowired AppTeamService appTeamService) {
        return new AppUserMongoConverter(appCapabilityService, appGroupService, appTeamService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppInviteConverter<AppInviteMongoEntity> appInviteConverter(@Autowired AppCapabilityService appCapabilityService, @Autowired AppGroupService appGroupService) {
        return new AppInviteMongoConverter(appCapabilityService, appGroupService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppGroupConverter<AppGroupMongoEntity> appGroupConverter(@Autowired AppCapabilityService appCapabilityService) {
        return new AppGroupMongoConverter(appCapabilityService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppClientConverter<AppClientMongoEntity> appClientConverter(@Autowired AppCapabilityService appCapabilityService) {
        return new AppClientMongoConverter(appCapabilityService);
    }
}
