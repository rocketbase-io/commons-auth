package io.rocketbase.commons.config;

import io.rocketbase.commons.service.auth.DefaultLoginService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.avatar.AvatarService;
import io.rocketbase.commons.service.avatar.GravatarService;
import io.rocketbase.commons.service.email.*;
import io.rocketbase.commons.service.forgot.AppUserForgotPasswordService;
import io.rocketbase.commons.service.forgot.DefaultAppUserForgotPasswordService;
import io.rocketbase.commons.service.impersonate.DefaultImpersonateService;
import io.rocketbase.commons.service.impersonate.ImpersonateService;
import io.rocketbase.commons.service.invite.AppInviteService;
import io.rocketbase.commons.service.invite.DefaultAppInviteService;
import io.rocketbase.commons.service.registration.DefaultRegistrationService;
import io.rocketbase.commons.service.registration.RegistrationService;
import io.rocketbase.commons.service.user.ActiveUserStore;
import io.rocketbase.commons.service.user.ActiveUserStoreLocalCache;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.user.DefaultAppUserService;
import io.rocketbase.commons.service.validation.DefaultValidationService;
import io.rocketbase.commons.service.validation.ValidationErrorCodeService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@AutoConfigureAfter({AuthAdapterAutoConfiguration.class})
@EnableConfigurationProperties({AuthProperties.class, EmailProperties.class, RegistrationProperties.class, GravatarProperties.class, UsernameProperties.class, PasswordProperties.class, JwtProperties.class})
@RequiredArgsConstructor
public class AuthServiceAutoConfiguration {

    private final AuthProperties authProperties;
    private final EmailProperties emailProperties;
    private final RegistrationProperties registrationProperties;
    private final GravatarProperties gravatarProperties;
    private final UsernameProperties usernameProperties;
    private final PasswordProperties passwordProperties;
    private final JwtProperties jwtProperties;

    @Bean
    @ConditionalOnMissingBean
    public MailContentConfig mailContentConfig() {
        return new DefaultMailContentConfig(emailProperties, authMessageSource());
    }

    @Bean
    @ConditionalOnMissingBean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStoreLocalCache(jwtProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @SneakyThrows
    public AuthEmailService emailService() {
        return new DefaultEmailService(new EmailAddress(emailProperties.getFromEmail(), emailProperties.getServiceName()));
    }

    @Bean
    @ConditionalOnMissingBean
    @SneakyThrows
    public EmailSender emailSender() {
        return new EmailLogSender();
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistrationService registrationService() {
        return new DefaultRegistrationService(authProperties, registrationProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    public AppInviteService appInviteService() {
        return new DefaultAppInviteService(authProperties);
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
    public ValidationErrorCodeService validationErrorCodeService() {
        return new ValidationErrorCodeService(usernameProperties, passwordProperties, authMessageSource());
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationService validationService() {
        return new DefaultValidationService(usernameProperties, passwordProperties, appUserService(), validationErrorCodeService());
    }

    @Bean(name = "authMessageSource")
    @ConditionalOnMissingBean
    public ResourceBundleMessageSource authMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("auth_messages");
        return messageSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public ImpersonateService impersonateService() {
        return new DefaultImpersonateService();
    }


}
