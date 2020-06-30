package io.rocketbase.commons.config;

import io.rocketbase.commons.api.ForgotPasswordApi;
import io.rocketbase.commons.api.InviteApi;
import io.rocketbase.commons.api.RegistrationApi;
import io.rocketbase.commons.api.ValidationApi;
import io.rocketbase.commons.controller.AuthFormsController;
import io.rocketbase.commons.controller.InviteFormsController;
import io.rocketbase.commons.controller.RegistrationFormsController;
import io.rocketbase.commons.resource.ForgotPasswordResource;
import io.rocketbase.commons.resource.InviteResource;
import io.rocketbase.commons.resource.RegistrationResource;
import io.rocketbase.commons.resource.ValidationResource;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public AuthFormsController authFormsController(@Autowired(required = false) ForgotPasswordApi forgotPasswordApi, @Autowired(required = false) ValidationApi validationApi) {
        ForgotPasswordApi forgotPassword = Nulls.notNull(forgotPasswordApi, new ForgotPasswordResource(authProperties.getBaseUrl()));
        ValidationApi validation = Nulls.notNull(validationApi, new ValidationResource(authProperties.getBaseUrl()));
        return new AuthFormsController(formsProperties, registrationProperties, authProperties, forgotPassword, validation);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "auth.registration.enabled", matchIfMissing = true)
    public RegistrationFormsController registrationFormsController(@Autowired(required = false) RegistrationApi registrationApi) {
        RegistrationApi api = Nulls.notNull(registrationApi, new RegistrationResource(authProperties.getBaseUrl()));
        return new RegistrationFormsController(formsProperties, registrationProperties, api);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "auth.invite.enabled", matchIfMissing = true)
    public InviteFormsController inviteFormsController(@Autowired(required = false) InviteApi inviteApi) {
        InviteApi api = Nulls.notNull(inviteApi, new InviteResource(authProperties.getBaseUrl()));
        return new InviteFormsController(formsProperties, registrationProperties, api);
    }

}
