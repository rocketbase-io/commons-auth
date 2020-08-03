package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "auth.registration")
@Validated
public class RegistrationProperties {

    private boolean enabled = true;

    /**
     * should use verify it's email-adress
     */
    private boolean verification = true;

    /**
     * in minutes - default 1 day
     */
    private long verificationExpiration = 1440;

    private String role = "USER";

}
