package io.rocketbase.commons.config;

import io.rocketbase.commons.service.email.MailContentConfig;
import io.rocketbase.commons.service.email.SimpleMailContentConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class BeanConfiguration {

    @Resource
    private EmailConfiguration emailConfiguration;

    @Bean
    @ConditionalOnMissingBean
    public MailContentConfig mailContentConfig() {
        return new SimpleMailContentConfig(emailConfiguration);
    }
}
