package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "auth.registration")
public class RegistrationConfiguration {

    private boolean enabled = true;
    private boolean emailValidation = true;
    private long emailValidationExpiration = 1440;
    private String role = "USER";
}
