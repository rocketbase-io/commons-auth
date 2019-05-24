package io.rocketbase.commons.config;

import io.rocketbase.commons.controller.AuthFormsController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({FormsProperties.class, RegistrationProperties.class})
@RequiredArgsConstructor
public class AuthFormAutoConfiguration {

    private final FormsProperties formsProperties;

    private final RegistrationProperties registrationProperties;

    @Bean
    @ConditionalOnMissingBean
    public AuthFormsController loginFormController(@Value("${auth.api.baseUrl}") String apiBaseUrl) {
        return new AuthFormsController(apiBaseUrl, formsProperties, registrationProperties);
    }

}
