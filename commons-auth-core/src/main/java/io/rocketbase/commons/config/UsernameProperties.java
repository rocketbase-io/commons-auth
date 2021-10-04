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
@ConfigurationProperties(prefix = "auth.username")
public class UsernameProperties {

    @Builder.Default
    private int minLength = 3;

    @Builder.Default
    private int maxLength = 20;

    @Builder.Default
    private String specialCharacters = ".-_";
}
