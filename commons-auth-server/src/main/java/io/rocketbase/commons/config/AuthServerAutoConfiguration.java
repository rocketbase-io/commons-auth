package io.rocketbase.commons.config;

import io.rocketbase.commons.controller.*;
import io.rocketbase.commons.controller.exceptionhandler.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({AuthAdapterAutoConfiguration.class, AuthServiceAutoConfiguration.class, CommonsRestAutoConfiguration.class})
@RequiredArgsConstructor
public class AuthServerAutoConfiguration {

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
    @ConditionalOnExpression(value = "${auth.invite.enabled:true}")
    public AppInviteController appInviteController() {
        return new AppInviteController();
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
    @ConditionalOnExpression(value = "${auth.invite.enabled:true}")
    public InviteController inviteController() {
        return new InviteController();
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

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "${auth.impersonate.enabled:true}")
    public ImpersonateController impersonateController() {
        return new ImpersonateController();
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
