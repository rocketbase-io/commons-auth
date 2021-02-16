package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Set;

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

    private Set<Long> capabilityIds = new HashSet<>();

    private Set<Long> groupIds = new HashSet<>();

}
