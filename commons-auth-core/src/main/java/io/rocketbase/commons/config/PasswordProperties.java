package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth.password")
public class PasswordProperties {

    private int minLength = 8;

    private int maxLength = 100;

    private int lowercase = 1;

    private int uppercase = 1;

    private int digit = 1;

    /**
     * character of this list:<br>
     * !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
     */
    private int special = 1;
}
