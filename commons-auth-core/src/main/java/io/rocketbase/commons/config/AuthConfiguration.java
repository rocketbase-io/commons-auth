package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Component
@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "auth")
@Validated
public class AuthConfiguration {

    private String roleNameAdmin = "ADMIN";
    private String roleNameUser = "USER";

    @NotNull
    private String keySecret;
    /**
     * cache time in minutes - 0 means disabled
     */
    private int userCacheTime = 30;


}
