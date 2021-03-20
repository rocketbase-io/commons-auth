package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.model.*;
import io.rocketbase.commons.service.capability.AppCapabilityJpaPersistenceService;
import io.rocketbase.commons.service.capability.AppCapabilityPersistenceService;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.client.AppClientJpaPersistenceService;
import io.rocketbase.commons.service.client.AppClientPersistenceService;
import io.rocketbase.commons.service.group.AppGroupJpaPersistenceService;
import io.rocketbase.commons.service.group.AppGroupPersistenceService;
import io.rocketbase.commons.service.invite.AppInviteJpaPersistenceService;
import io.rocketbase.commons.service.invite.AppInvitePersistenceService;
import io.rocketbase.commons.service.team.AppTeamJpaPersistenceService;
import io.rocketbase.commons.service.team.AppTeamPersistenceService;
import io.rocketbase.commons.service.team.AppTeamService;
import io.rocketbase.commons.service.user.AppUserJpaPersistenceService;
import io.rocketbase.commons.service.user.AppUserPersistenceService;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

@Configuration
@AutoConfigureBefore(AuthServiceAutoConfiguration.class)
public class AuthJpaAutoConfiguration {

    @Resource
    private AppCapabilityConverter appCapabilityConverter;

    @Resource
    private AppTeamService appTeamService;

    @Bean
    @ConditionalOnMissingBean
    public AppUserPersistenceService<AppUserJpaEntity> appUserPersistenceService(@Autowired EntityManager entityManager,
                                                                                 @Autowired AppGroupPersistenceService groupPersistenceService, @Autowired AppCapabilityPersistenceService capabilityPersistenceService, @Autowired AppTeamPersistenceService teamPersistenceService) {
        return new AppUserJpaPersistenceService(entityManager, groupPersistenceService, capabilityPersistenceService, teamPersistenceService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppInvitePersistenceService<AppInviteJpaEntity> appInvitePersistenceService(@Autowired EntityManager entityManager, @Autowired Snowflake snowflake) {
        return new AppInviteJpaPersistenceService(entityManager, snowflake);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppGroupPersistenceService<AppGroupJpaEntity> appGroupPersistenceService(@Autowired EntityManager entityManager, @Autowired Snowflake snowflake) {
        return new AppGroupJpaPersistenceService(entityManager, snowflake);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppTeamPersistenceService<AppTeamJpaEntity> appTeamPersistenceService(@Autowired EntityManager entityManager, @Autowired Snowflake snowflake) {
        return new AppTeamJpaPersistenceService(entityManager, snowflake);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppCapabilityPersistenceService<AppCapabilityJpaEntity> appCapabilityPersistenceService(@Autowired EntityManager entityManager, @Autowired Snowflake snowflake) {
        return new AppCapabilityJpaPersistenceService(entityManager, snowflake);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppClientPersistenceService<AppClientJpaEntity> appClientPersistenceService(@Autowired EntityManager entityManager, @Autowired Snowflake snowflake) {
        return new AppClientJpaPersistenceService(entityManager, snowflake);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserConverter<AppUserJpaEntity> appUserConverter(@Autowired AppGroupConverter<AppGroupJpaEntity> appGroupConverter, @Autowired AppCapabilityService appCapabilityService) {
        return new AppUserJpaConverter(appGroupConverter, appCapabilityConverter, appCapabilityService, appTeamService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppInviteConverter<AppInviteJpaEntity> appInviteConverter(@Autowired AppGroupConverter<AppGroupJpaEntity> appGroupConverter) {
        return new AppInviteJpaConverter(appCapabilityConverter, appGroupConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppGroupConverter<AppGroupJpaEntity> appGroupConverter() {
        return new AppGroupJpaConverter(appCapabilityConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppClientConverter<AppClientJpaEntity> appClientConverter() {
        return new AppClientJpaConverter(appCapabilityConverter);
    }
}
