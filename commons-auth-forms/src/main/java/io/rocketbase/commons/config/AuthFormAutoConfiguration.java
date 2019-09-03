package io.rocketbase.commons.config;

import io.rocketbase.commons.controller.AuthFormsController;
import io.rocketbase.commons.controller.RegistrationFormsController;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuthProperties.class, FormsProperties.class, RegistrationProperties.class})
@RequiredArgsConstructor
public class AuthFormAutoConfiguration {

    private final AuthProperties authProperties;

    private final FormsProperties formsProperties;

    private final RegistrationProperties registrationProperties;

    @Bean
    @ConditionalOnMissingBean
    public AuthFormsController authFormsController() {
        return new AuthFormsController(authProperties, formsProperties, registrationProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "auth.registration.enabled", matchIfMissing = true)
    public RegistrationFormsController registrationFormsController() {
        return new RegistrationFormsController(authProperties, formsProperties, registrationProperties);
    }

}
