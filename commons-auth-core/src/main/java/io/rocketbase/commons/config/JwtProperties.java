package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {


    private String header = HttpHeaders.AUTHORIZATION;
    private String tokenPrefix = "Bearer ";
    private String uriParam = "token";

    @NotNull
    private String secret;

    /**
     * default 1 hour
     */
    private long accessTokenExpiration = 60;

    /**
     * default 30 days
     */
    private long refreshTokenExpiration = 43200;

}
