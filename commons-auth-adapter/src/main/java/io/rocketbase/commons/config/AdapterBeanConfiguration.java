package io.rocketbase.commons.config;

import io.rocketbase.commons.adapters.AuthClientRequestFactory;
import io.rocketbase.commons.adapters.AuthRestTemplate;
import io.rocketbase.commons.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdapterBeanConfiguration {

    @Value("${auth.api.baseUrl}")
    private String baseAuthApiUrl;

    @Bean
    public AuthRestTemplate authRestTemplate() {
        return new AuthRestTemplate(new AuthClientRequestFactory());
    }

    @Bean
    @ConditionalOnMissingBean
    public LoginResource loginResource() {
        return new LoginResource(baseAuthApiUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistrationResource registrationResource() {
        return new RegistrationResource(baseAuthApiUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationResource validationResource() {
        return new ValidationResource(baseAuthApiUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public ForgotPasswordResource forgotPasswordResource() {
        return new ForgotPasswordResource(baseAuthApiUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationResource authenticationResource(@Autowired AuthRestTemplate authRestTemplate) {
        return new AuthenticationResource(baseAuthApiUrl, authRestTemplate);
    }
}
