package io.rocketbase.commons.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties(prefix = "auth.registration")
@Validated
public class RegistrationProperties {

    @Builder.Default
    private boolean enabled = true;

    /**
     * should use verify it's email-adress
     */
    @Builder.Default
    private boolean verification = true;

    @Builder.Default
    private Duration verificationExpiration = Duration.of(1, ChronoUnit.DAYS);

    @Builder.Default
    private Set<Long> capabilityIds = new HashSet<>();

    @Builder.Default
    private Set<Long> groupIds = new HashSet<>();

}
