package io.rocketbase.commons.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties(prefix = "auth.password")
public class PasswordProperties {

    @Builder.Default
    private int minLength = 8;

    @Builder.Default
    private int maxLength = 100;

    @Builder.Default
    private int lowercase = 1;

    @Builder.Default
    private int uppercase = 1;

    @Builder.Default
    private int digit = 1;

    /**
     * character of this list:<br>
     * !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
     */
    @Builder.Default
    private int special = 1;
}
