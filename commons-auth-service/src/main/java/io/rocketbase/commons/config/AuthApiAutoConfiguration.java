package io.rocketbase.commons.config;

import io.rocketbase.commons.api.*;
import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.forgot.AppUserForgotPasswordService;
import io.rocketbase.commons.service.impersonate.ImpersonateService;
import io.rocketbase.commons.service.invite.AppInviteService;
import io.rocketbase.commons.service.registration.RegistrationService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.validation.ValidationService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@AutoConfigureAfter({AuthServiceAutoConfiguration.class})
public class AuthApiAutoConfiguration {

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private RegistrationService registrationService;

    @Resource
    private ValidationService validationService;

    @Resource
    private LoginService loginService;

    @Resource
    private AppUserForgotPasswordService forgotPasswordService;

    @Resource
    private AppInviteService appInviteService;

    @Resource
    private AppInviteConverter inviteConverter;

    @Resource
    private ImpersonateService impersonateService;

    @Bean
    @ConditionalOnMissingBean
    public AppInviteApi appInviteApi() {
        return new AppInviteServiceApi(appInviteService, inviteConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserApi appUserApi() {
        return new AppUserServiceApi(appUserService, appUserConverter, validationService, appInviteService, inviteConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationApi authenticationApi() {
        return new AuthenticationServiceApi(appUserService, appUserConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public ForgotPasswordApi forgotPasswordApi() {
        return new ForgotPasswordServiceApi(forgotPasswordService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ImpersonateApi impersonateApi() {
        return new ImpersonateServiceApi(impersonateService, appUserService);
    }

    @Bean
    @ConditionalOnMissingBean
    public InviteApi inviteApi() {
        return new InviteServiceApi(appInviteService, inviteConverter, appUserConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public LoginApi loginApi() {
        return new LoginServiceApi(loginService, jwtTokenService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistrationApi registrationApi() {
        return new RegistrationServiceApi(registrationService, appUserConverter, jwtTokenService);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserSearchApi userSearchApi() {
        return new UserSearchServiceApi(appUserService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationApi validationApi() {
        return new ValidationServiceApi(validationService);
    }

}
