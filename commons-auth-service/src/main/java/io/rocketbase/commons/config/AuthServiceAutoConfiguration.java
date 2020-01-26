package io.rocketbase.commons.config;

import io.rocketbase.commons.service.auth.DefaultLoginService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.avatar.AvatarService;
import io.rocketbase.commons.service.avatar.GravatarService;
import io.rocketbase.commons.service.email.DefaultEmailService;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.email.MailContentConfig;
import io.rocketbase.commons.service.email.SimpleMailContentConfig;
import io.rocketbase.commons.service.forgot.AppUserForgotPasswordService;
import io.rocketbase.commons.service.forgot.DefaultAppUserForgotPasswordService;
import io.rocketbase.commons.service.registration.DefaultRegistrationService;
import io.rocketbase.commons.service.registration.RegistrationService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.user.DefaultAppUserService;
import io.rocketbase.commons.service.validation.DefaultValidationService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.internet.InternetAddress;

@Configuration
@AutoConfigureAfter({AuthAdapterAutoConfiguration.class})
@EnableConfigurationProperties({AuthProperties.class, EmailProperties.class, RegistrationProperties.class, GravatarProperties.class, UsernameProperties.class, PasswordProperties.class})
@RequiredArgsConstructor
public class AuthServiceAutoConfiguration {

    private final AuthProperties authProperties;
    private final EmailProperties emailProperties;
    private final RegistrationProperties registrationProperties;
    private final GravatarProperties gravatarProperties;
    private final UsernameProperties usernameProperties;
    private final PasswordProperties passwordProperties;

    @Bean
    @ConditionalOnMissingBean
    public MailContentConfig mailContentConfig() {
        return new SimpleMailContentConfig(emailProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @SneakyThrows
    public EmailService emailService() {
        return new DefaultEmailService(new InternetAddress(emailProperties.getFromEmail(), emailProperties.getServiceName()));
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistrationService registrationService() {
        return new DefaultRegistrationService(authProperties, registrationProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserForgotPasswordService appUserForgotPasswordService() {
        return new DefaultAppUserForgotPasswordService(authProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserService appUserService() {
        return new DefaultAppUserService(authProperties, registrationProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    public LoginService loginService() {
        return new DefaultLoginService();
    }

    @Bean
    @ConditionalOnMissingBean
    public AvatarService avatarService() {
        return new GravatarService(gravatarProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationService validationService() {
        return new DefaultValidationService(usernameProperties, passwordProperties, appUserService());
    }
}
