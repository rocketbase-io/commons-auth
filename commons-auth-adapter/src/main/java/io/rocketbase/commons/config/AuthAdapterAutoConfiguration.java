package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.security.CustomAuthoritiesProvider;
import io.rocketbase.commons.security.EmptyCustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class})
@RequiredArgsConstructor
public class AuthAdapterAutoConfiguration {

    private final JwtProperties jwtProperties;

    @Bean
    @ConditionalOnMissingBean
    public CustomAuthoritiesProvider customAuthoritiesProvider() {
        return new EmptyCustomAuthoritiesProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenService jwtTokenService(@Autowired CustomAuthoritiesProvider customAuthoritiesProvider) {
        return new JwtTokenService(jwtProperties, customAuthoritiesProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppUserConverter appUserConverter() {
        return new AppUserConverter();
    }

}
