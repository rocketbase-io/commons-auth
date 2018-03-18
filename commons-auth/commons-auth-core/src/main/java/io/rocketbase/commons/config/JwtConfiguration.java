package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtConfiguration {

    private String header = "Authorization";
    private String tokenPrefix = "Bearer ";
    private String uriParam = "token";

    @NotNull
    private String secret;

    private Expiration expiration;

    @Data
    public static class Expiration {
        private long accessToken = 60;
        private long refreshToken = 43200;
    }
}
