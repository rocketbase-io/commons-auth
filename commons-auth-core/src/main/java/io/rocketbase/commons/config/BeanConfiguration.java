package io.rocketbase.commons.config;

import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.*;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.email.MailContentConfig;
import io.rocketbase.commons.service.email.SimpleMailContentConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuthProperties.class, JwtProperties.class, EmailProperties.class, RegistrationProperties.class, GravatarProperties.class})
@RequiredArgsConstructor
public class BeanConfiguration {

    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;
    private final EmailProperties emailProperties;
    private final RegistrationProperties registrationProperties;
    private final GravatarProperties gravatarProperties;

    @Bean
    @ConditionalOnMissingBean
    public MailContentConfig mailContentConfig() {
        return new SimpleMailContentConfig(emailProperties);
    }

    @Bean
    public EmailService emailService() {
        return new EmailService(authProperties, emailProperties);
    }

    @Bean
    public AppUserRegistrationService appUserRegistrationService() {
        return new AppUserRegistrationService(registrationProperties);
    }

    @Bean
    public AppUserForgotPasswordService appUserForgotPasswordService() {
        return new AppUserForgotPasswordService(authProperties);
    }

    @Bean
    public JwtTokenService jwtTokenService() {
        return new JwtTokenService(jwtProperties);
    }

    @Bean
    public AppUserService appUserService() {
        return new AppUserService(authProperties, registrationProperties);
    }

    @Bean
    public GravatarService gravatarService() {
        return new GravatarService(gravatarProperties);
    }


    @Bean
    public TokenizerService tokenizerService() {
        return new TokenizerService(authProperties.getTokenSecret());
    }
}