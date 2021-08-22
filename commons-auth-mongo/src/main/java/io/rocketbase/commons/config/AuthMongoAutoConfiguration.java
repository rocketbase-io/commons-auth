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
import io.rocketbase.commons.service.token.AuthorizationCodeMongoService;
import io.rocketbase.commons.service.token.AuthorizationCodeService;
import io.rocketbase.commons.service.user.AppUserMongoPersistenceService;
import io.rocketbase.commons.service.user.AppUserPersistenceService;
import io.rocketbase.commons.util.CommonsAuthCollectionNameResolver;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@AutoConfigureBefore(AuthServiceAutoConfiguration.class)
public class AuthMongoAutoConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Snowflake snowflake;

    @Bean
    @ConditionalOnMissingBean
    public AppUserPersistenceService<AppUserMongoEntity> appUserPersistenceService(@Autowired CommonsAuthCollectionNameResolver collectionNameResolver) {
        return new AppUserMongoPersistenceService(mongoTemplate, collectionNameResolver.user());
    }

    @Bean
    @ConditionalOnMissingBean
    public AppInvitePersistenceService<AppInviteMongoEntity> appInvitePersistenceService(@Autowired CommonsAuthCollectionNameResolver collectionNameResolver) {
        return new AppInviteMongoPersistenceService(mongoTemplate, snowflake, collectionNameResolver.invite());
    }

    @Bean
    @ConditionalOnMissingBean
    public AppCapabilityPersistenceService<AppCapabilityMongoEntity> appCapabilityPersistenceService(@Autowired CommonsAuthCollectionNameResolver collectionNameResolver, @Value("${auth.capability.init:true}") boolean initializeRoot) {
        return new AppCapabilityMongoPersistenceService(mongoTemplate, snowflake, collectionNameResolver.capability(), initializeRoot);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppTeamPersistenceService<AppTeamMongoEntity> appTeamPersistenceService(@Autowired CommonsAuthCollectionNameResolver collectionNameResolver) {
        return new AppTeamMongoPersistenceService(mongoTemplate, snowflake, collectionNameResolver.team());
    }

    @Bean
    @ConditionalOnMissingBean
    public AppGroupPersistenceService<AppGroupMongoEntity> appGroupPersistenceService(@Autowired CommonsAuthCollectionNameResolver collectionNameResolver) {
        return new AppGroupMongoPersistenceService(mongoTemplate, snowflake, collectionNameResolver.group());
    }

    @Bean
    @ConditionalOnMissingBean
    public AppClientPersistenceService<AppClientMongoEntity> appClientPersistenceService(@Autowired CommonsAuthCollectionNameResolver collectionNameResolver) {
        return new AppClientMongoPersistenceService(mongoTemplate, snowflake, collectionNameResolver.client());
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserConverter<AppUserMongoEntity> appUserConverter(@Autowired AppCapabilityService appCapabilityService, @Autowired AppGroupService appGroupService, @Autowired AppGroupConverter appGroupConverter, @Autowired AppTeamService appTeamService, @Autowired AppTeamConverter appTeamConverter) {
        return new AppUserMongoConverter(appCapabilityService, appGroupService, appGroupConverter, appTeamService, appTeamConverter);
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

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationCodeService authorizationCodeService(@Autowired CommonsAuthCollectionNameResolver collectionNameResolver) {
        return new AuthorizationCodeMongoService(mongoTemplate, collectionNameResolver.authcode());
    }
}
