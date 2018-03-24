package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "auth")
public class AuthConfiguration {

    private String roleNameAdmin = "ADMIN";
    private String roleNameUser = "USER";
    private String keySecret = "QENtCtMfH7pv2Qf6GWBcCEDMApQC62SA";
    /**
     * cache time in minutes - 0 means disabled
     */
    private int userCacheTime = 30;


}
