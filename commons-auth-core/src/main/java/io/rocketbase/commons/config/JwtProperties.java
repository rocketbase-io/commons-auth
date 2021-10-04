package io.rocketbase.commons.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    @Builder.Default
    private String header = HttpHeaders.AUTHORIZATION;

    @Builder.Default
    private String tokenPrefix = "Bearer ";

    @Builder.Default
    private String uriParam = "token";

    @NotNull
    private String secret;

    @Builder.Default
    private Duration accessTokenExpiration = Duration.of(15, ChronoUnit.MINUTES);

    @Builder.Default
    private Duration refreshTokenExpiration = Duration.of(30, ChronoUnit.DAYS);

}
