package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "auth")
public class AuthConfiguration {

    private RegistrationConfiguration registration;
    private RoleConfiguration role;
    private JwtConfiguration jwt;


    @Data
    public static class RegistrationConfiguration {
        private boolean enabled;
        private boolean emailValidation;
        private String role;
    }

    @Data
    public static class RoleConfiguration {
        private String admin;
        private String user;
    }

    @Data
    public static class JwtConfiguration {

        private String header;
        private String tokenPrefix;
        private String uriParam;
        private String secret;
        private Expiration expiration;

        @Data
        public static class Expiration {
            private Long accessToken;
            private Long refreshToken;
        }
    }

}
