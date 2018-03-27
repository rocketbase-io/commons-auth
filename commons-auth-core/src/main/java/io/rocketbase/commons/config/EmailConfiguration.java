package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "auth.email")
public class EmailConfiguration {

    private String subjectPrefix = "[RocketAuth]";
    private String serviceName = "rocketbase-commons-auth";
    private String supportEmail = "support@localhost";
    private String fromEmail = "no-reply@localhost";
    private String copyrightName = "rocketbase.io";
    private String copyrightUrl = "https://www.rocketbase.io";
    /**
     * full qualified url to a custom UI that proceed the verification<br>
     * ?verification=VALUE will get append
     */
    private String verificationUrl = null;

    /**
     * full qualified url to a custom UI that proceed the password reset<br>
     * ?verification=VALUE will get append
     */
    private String passwordResetUrl = null;
    private long passwordResetExpiration = 60;

}
