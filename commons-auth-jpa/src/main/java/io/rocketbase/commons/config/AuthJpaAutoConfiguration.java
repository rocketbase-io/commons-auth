package io.rocketbase.commons.config;

import io.rocketbase.commons.repository.AppUserRepository;
import io.rocketbase.commons.service.AppUserJpaServiceImpl;
import io.rocketbase.commons.service.AppUserPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AppUserPersistenceService appUserPersistenceService(@Autowired AppUserRepository appUserRepository) {
        return new AppUserJpaServiceImpl(appUserRepository);
    }
}
