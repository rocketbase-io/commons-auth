package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.AppCapabilityConverter;
import io.rocketbase.commons.converter.AppGroupConverter;
import io.rocketbase.commons.converter.AppTeamConverter;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.service.auth.DefaultLoginService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.avatar.AvatarService;
import io.rocketbase.commons.service.avatar.GravatarService;
import io.rocketbase.commons.service.capability.AppCapabilityPersistenceService;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.capability.DefaultAppCapabilityService;
import io.rocketbase.commons.service.change.ChangeAppUserWithConfirmService;
import io.rocketbase.commons.service.change.DefaultChangeAppUserWithConfirmService;
import io.rocketbase.commons.service.client.AppClientPersistenceService;
import io.rocketbase.commons.service.client.AppClientService;
import io.rocketbase.commons.service.client.DefaultAppClientService;
import io.rocketbase.commons.service.email.*;
import io.rocketbase.commons.service.forgot.AppUserForgotPasswordService;
import io.rocketbase.commons.service.forgot.DefaultAppUserForgotPasswordService;
import io.rocketbase.commons.service.group.AppGroupPersistenceService;
import io.rocketbase.commons.service.group.AppGroupService;
import io.rocketbase.commons.service.group.DefaultAppGroupService;
import io.rocketbase.commons.service.impersonate.DefaultImpersonateService;
import io.rocketbase.commons.service.impersonate.ImpersonateService;
import io.rocketbase.commons.service.initialize.DataInitializerService;
import io.rocketbase.commons.service.initialize.DefaultDataInitializerServiceService;
import io.rocketbase.commons.service.invite.AppInviteService;
import io.rocketbase.commons.service.invite.DefaultAppInviteService;
import io.rocketbase.commons.service.registration.DefaultRegistrationService;
import io.rocketbase.commons.service.registration.RegistrationService;
import io.rocketbase.commons.service.team.AppTeamPersistenceService;
import io.rocketbase.commons.service.team.AppTeamService;
import io.rocketbase.commons.service.team.DefaultAppTeamService;
import io.rocketbase.commons.service.user.*;
import io.rocketbase.commons.service.validation.DefaultValidationService;
import io.rocketbase.commons.service.validation.ValidationErrorCodeService;
import io.rocketbase.commons.service.validation.ValidationService;
import io.rocketbase.commons.util.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Snowflake snowflake() {
        return new Snowflake();
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
    public AppCapabilityService appCapabilityService(@Autowired AppCapabilityPersistenceService appCapabilityPersistenceService, @Autowired AppCapabilityConverter appCapabilityConverter) {
        return new DefaultAppCapabilityService(appCapabilityPersistenceService, appCapabilityConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppClientService appClientService(@Autowired AppClientPersistenceService appClientPersistenceService) {
        return new DefaultAppClientService(appClientPersistenceService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppGroupService appGroupService(@Autowired AppGroupPersistenceService appGroupPersistenceService, @Autowired AppGroupConverter appGroupConverter) {
        return new DefaultAppGroupService(appGroupPersistenceService, appGroupConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppTeamService appTeamService(@Autowired AppTeamPersistenceService appTeamPersistenceService, @Autowired AppTeamConverter appTeamConverter) {
        return new DefaultAppTeamService(appTeamPersistenceService, appTeamConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppTeamConverter appTeamConverter() {
        return new AppTeamConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public AppCapabilityConverter appCapabilityConverter() {
        return new AppCapabilityConverter();
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
    public AppUserTokenService appUserTokenService(@Autowired AppUserService appUserService, @Autowired AppUserConverter appUserConverter) {
        return new DefaultAppUserTokenService(appUserService, appUserConverter);
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
    public ValidationService validationService(@Autowired AppUserPersistenceService appUserPersistenceService) {
        return new DefaultValidationService(usernameProperties, passwordProperties, appUserPersistenceService, validationErrorCodeService());
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

    @Bean
    @ConditionalOnMissingBean
    public ChangeAppUserWithConfirmService changeAppUserWithConfirmService() {
        return new DefaultChangeAppUserWithConfirmService(authProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataInitializerService dataInitializerService(@Autowired AppUserService appUserService, @Autowired AppCapabilityService appCapabilityService, @Autowired AppClientService appClientService) {
        return new DefaultDataInitializerServiceService(appUserService, appCapabilityService, appClientService);
    }

}
