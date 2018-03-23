package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtConfiguration {

    private String header = HttpHeaders.AUTHORIZATION;
    private String tokenPrefix = "Bearer ";
    private String uriParam = "token";

    @NotNull
    private String secret;

    private long accessTokenExpiration = 60;
    private long refreshTokenExpiration = 43200;
}
