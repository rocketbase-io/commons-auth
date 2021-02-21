package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.converter.AppInviteJpaConverter;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.converter.AppUserJpaConverter;
import io.rocketbase.commons.model.AppInviteJpaEntity;
import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.service.capability.AppCapabilityJpaPersistenceService;
import io.rocketbase.commons.service.group.AppGroupJpaPersistenceService;
import io.rocketbase.commons.service.invite.AppInviteJpaPersistenceService;
import io.rocketbase.commons.service.invite.AppInvitePersistenceService;
import io.rocketbase.commons.service.team.AppTeamJpaPersistenceService;
import io.rocketbase.commons.service.user.AppUserJpaPersistenceService;
import io.rocketbase.commons.service.user.AppUserPersistenceService;
import io.rocketbase.commons.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@AutoConfigureBefore(AuthServiceAutoConfiguration.class)
public class AuthJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AppUserPersistenceService<AppUserJpaEntity> appUserPersistenceService(@Autowired EntityManager entityManager,
                                                                                 @Autowired AppGroupJpaPersistenceService groupJpaPersistenceService, @Autowired AppCapabilityJpaPersistenceService capabilityJpaPersistenceService, @Autowired AppTeamJpaPersistenceService teamJpaPersistenceService) {
        return new AppUserJpaPersistenceService(entityManager, groupJpaPersistenceService, capabilityJpaPersistenceService, teamJpaPersistenceService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppInvitePersistenceService<AppInviteJpaEntity> appInvitePersistenceService(@Autowired EntityManager entityManager, @Autowired Snowflake snowflake) {
        return new AppInviteJpaPersistenceService(entityManager, snowflake);
    }


    @Bean
    @ConditionalOnMissingBean
    public AppUserConverter<AppUserJpaEntity> appUserConverter() {
        return new AppUserJpaConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public AppInviteConverter<AppInviteJpaEntity> appInviteConverter() {
        return new AppInviteJpaConverter();
    }
}
