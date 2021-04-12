package io.rocketbase.commons.config;

import io.rocketbase.commons.api.*;
import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.change.ChangeAppUserWithConfirmService;
import io.rocketbase.commons.service.client.AppClientService;
import io.rocketbase.commons.service.forgot.AppUserForgotPasswordService;
import io.rocketbase.commons.service.group.AppGroupService;
import io.rocketbase.commons.service.impersonate.ImpersonateService;
import io.rocketbase.commons.service.invite.AppInviteService;
import io.rocketbase.commons.service.registration.RegistrationService;
import io.rocketbase.commons.service.team.AppTeamService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.user.AppUserTokenService;
import io.rocketbase.commons.service.validation.ValidationService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
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
    private AppUserTokenService appUserTokenService;

    @Resource
    private RegistrationService registrationService;

    @Resource
    private ValidationService validationService;

    @Resource
    private AppUserForgotPasswordService forgotPasswordService;

    @Resource
    private AppInviteService appInviteService;

    @Resource
    private AppInviteConverter inviteConverter;

    @Resource
    private ImpersonateService impersonateService;

    @Resource
    private ChangeAppUserWithConfirmService changeAppUserWithConfirmService;

    @Resource
    private LoginService loginService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private AppCapabilityService appCapabilityService;

    @Resource
    private AppCapabilityConverter appCapabilityConverter;

    @Resource
    private AppClientService appClientService;

    @Resource
    private AppClientConverter appClientConverter;

    @Resource
    private AppGroupService appGroupService;

    @Resource
    private AppGroupConverter appGroupConverter;

    @Resource
    private AppTeamService appTeamService;

    @Resource
    private AppTeamConverter appTeamConverter;

    @Bean
    @ConditionalOnMissingBean
    public AppInviteApi appInviteApi() {
        return new AppInviteApiService(appInviteService, inviteConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserApi appUserApi() {
        return new AppUserApiService(appUserService, appUserConverter, validationService, appInviteService, inviteConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationApi authenticationApi() {
        return new AuthenticationApiService(appUserService, appUserConverter, changeAppUserWithConfirmService, applicationEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public ForgotPasswordApi forgotPasswordApi() {
        return new ForgotPasswordApiService(forgotPasswordService, appUserConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public ImpersonateApi impersonateApi() {
        return new ImpersonateApiService(impersonateService, appUserTokenService);
    }

    @Bean
    @ConditionalOnMissingBean
    public InviteApi inviteApi() {
        return new InviteApiService(appInviteService, inviteConverter, appUserConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public LoginApi loginApi() {
        return new LoginApiService(loginService, jwtTokenService, appUserTokenService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistrationApi registrationApi() {
        return new RegistrationApiService(registrationService, appUserConverter, jwtTokenService);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserSearchApi userSearchApi() {
        return new UserSearchApiService(appUserService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationApi validationApi() {
        return new ValidationApiService(validationService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppCapabilityApi appCapabilityApi() {
        return new AppCapabilityApiService(appCapabilityService, appCapabilityConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppClientApi appClientApi() {
        return new AppClientApiService(appClientService, appClientConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppGroupApi appGroupApi() {
        return new AppGroupApiService(appGroupService, appGroupConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppTeamApi appTeamApi() {
        return new AppTeamApiService(appTeamService, appTeamConverter);
    }

}
