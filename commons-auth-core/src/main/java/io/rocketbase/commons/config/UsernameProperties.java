package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth.username")
public class UsernameProperties {

    private int minLength = 3;

    private int maxLength = 20;

    private String specialCharacters = ".-_";
}
