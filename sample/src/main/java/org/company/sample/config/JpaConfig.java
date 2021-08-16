package org.company.sample.config;

import io.rocketbase.commons.security.CommonsPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditProvider")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditProvider() {
        return new SpringSecurityAuditorAware();
    }

    public static class SpringSecurityAuditorAware implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            CommonsPrincipal current = CommonsPrincipal.getCurrent();
            if (current != null) {
                return Optional.of(current.getUsername());
            }
            return Optional.empty();
        }
    }


}
