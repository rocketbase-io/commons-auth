package io.rocketbase.commons.config;

import io.rocketbase.commons.security.CustomAuthoritiesProvider;
import io.rocketbase.commons.security.EmptyCustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.*;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.email.MailContentConfig;
import io.rocketbase.commons.service.email.SimpleMailContentConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.internet.InternetAddress;

@Configuration
@EnableConfigurationProperties({AuthProperties.class, JwtProperties.class, EmailProperties.class, RegistrationProperties.class, GravatarProperties.class, UsernameProperties.class, PasswordProperties.class})
@RequiredArgsConstructor
public class BeanConfiguration {

    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;
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

    @SneakyThrows
    @Bean
    public EmailService emailService() {
        return new EmailService(new InternetAddress(emailProperties.getFromEmail(), emailProperties.getServiceName()));
    }

    @Bean
    public AppUserRegistrationService appUserRegistrationService() {
        return new AppUserRegistrationService(authProperties, registrationProperties);
    }

    @Bean
    public AppUserForgotPasswordService appUserForgotPasswordService() {
        return new AppUserForgotPasswordService(authProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomAuthoritiesProvider customAuthoritiesProvider() {
        return new EmptyCustomAuthoritiesProvider();
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

    @Bean
    public ValidationService validationService() {
        return new ValidationService(usernameProperties, passwordProperties, appUserService());
    }
}
