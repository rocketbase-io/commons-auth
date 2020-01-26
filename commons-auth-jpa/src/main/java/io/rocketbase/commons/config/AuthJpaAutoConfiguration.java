package io.rocketbase.commons.config;

import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.repository.AppUserJpaRepository;
import io.rocketbase.commons.service.AppUserJpaServiceImpl;
import io.rocketbase.commons.service.AppUserPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(AuthServiceAutoConfiguration.class)
public class AuthJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AppUserPersistenceService<AppUserJpaEntity> appUserPersistenceService(@Autowired AppUserJpaRepository appUserJpaRepository) {
        return new AppUserJpaServiceImpl(appUserJpaRepository);
    }
}
