package io.rocketbase.commons.config;

import io.rocketbase.commons.security.CustomAuthoritiesProvider;
import io.rocketbase.commons.security.EmptyCustomAuthoritiesProvider;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.JwtTokenStoreProvider;
import io.rocketbase.commons.util.JwtTokenStoreHttp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, AuthProperties.class})
@RequiredArgsConstructor
public class AuthAdapterAutoConfiguration {

    private final JwtProperties jwtProperties;
    private final AuthProperties authProperties;

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
    public JwtTokenStoreProvider jwtTokenStoreProvider() {
        return tokenBundle -> new JwtTokenStoreHttp(authProperties.getBaseUrl(), tokenBundle);
    }

}
