package io.rocketbase.commons.config;

import io.rocketbase.commons.controller.*;
import io.rocketbase.commons.controller.exceptionhandler.*;
import io.rocketbase.commons.service.*;
import io.rocketbase.commons.service.email.DefaultEmailService;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.email.MailContentConfig;
import io.rocketbase.commons.service.email.SimpleMailContentConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
    public AppUserRegistrationService appUserRegistrationService() {
        return new AppUserRegistrationService(authProperties, registrationProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserForgotPasswordService appUserForgotPasswordService() {
        return new AppUserForgotPasswordService(authProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserService appUserService() {
        return new AppUserService(authProperties, registrationProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public GravatarService gravatarService() {
        return new GravatarService(gravatarProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenizerService tokenizerService() {
        return new TokenizerService(authProperties.getTokenSecret());
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationService validationService() {
        return new ValidationService(usernameProperties, passwordProperties, appUserService());
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
    public RegistrationController registrationController() {
        return new RegistrationController();
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
