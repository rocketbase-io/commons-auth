package io.rocketbase.commons.config;

import io.rocketbase.commons.controller.*;
import io.rocketbase.commons.controller.exceptionhandler.*;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.internet.InternetAddress;

@Configuration
@AutoConfigureAfter({AuthAdapterAutoConfiguration.class, CommonsRestAutoConfiguration.class})
@EnableConfigurationProperties({AuthProperties.class, EmailProperties.class, RegistrationProperties.class, GravatarProperties.class, UsernameProperties.class, PasswordProperties.class})
@RequiredArgsConstructor
public class AuthServerAutoConfiguration {

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

    // -------------------------------------------------------
    // --------------------- Controller ----------------------
    // -------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public AppUserController appUserController() {
        return new AppUserController();
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuthLoginRefreshController oAuthLoginRefreshController() {
        return new OAuthLoginRefreshController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationController authenticationController() {
        return new AuthenticationController();
    }

    @Bean
    @ConditionalOnMissingBean
    public ForgotPasswordController forgotPasswordController() {
        return new ForgotPasswordController();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "${auth.registration.enabled:true}")
    public RegistrationController registrationController() {
        return new RegistrationController();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "${auth.search.enabled:true}")
    public UserSearchController userSearchController() {
        return new UserSearchController();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationController validationController() {
        return new ValidationController();
    }

    // -------------------------------------------------------
    // ------------------ ExceptionHandlers ------------------
    // -------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public EmailValidationExceptionHandler emailValidationExceptionHandler() {
        return new EmailValidationExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordValidationExceptionHandler passwordValidationExceptionHandler() {
        return new PasswordValidationExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistrationExceptionHandler registrationExceptionHandler() {
        return new RegistrationExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public UnknownUserExceptionHandler unknownUserExceptionHandler() {
        return new UnknownUserExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public UsernameValidationExceptionHandler usernameValidationExceptionHandler() {
        return new UsernameValidationExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public VerificationExceptionHandler verificationExceptionHandler() {
        return new VerificationExceptionHandler();
    }
}
