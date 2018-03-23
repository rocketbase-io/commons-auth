package io.rocketbase.commons.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public final class JwtTokenDecoder {

    public static JwtTokenBody decodeTokenBody(String token) {
        if (token != null) {
            String[] split = token.split("\\.");
            if (split.length == 3) {
                try {
                    byte[] body = Base64.getDecoder().decode(split[1]);
                    return new ObjectMapper().readValue(body, JwtTokenBody.class);
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JwtTokenBody {
        /**
         * token creation date
         */
        private Long iat;
        /**
         * expiration
         */
        private Long exp;
        /**
         * username
         */
        private String sub;
        /**
         * roles
         */
        private List<String> scopes;

        public boolean isExpired() {
            if (exp != null) {
                return getExpiration().isBefore(LocalDateTime.now());
            }
            return false;
        }

        public LocalDateTime getExpiration() {
            if (exp != null) {
                return LocalDateTime.ofInstant(new Date(exp * 1000).toInstant(), ZoneId.systemDefault());
            }
            return null;
        }

        public LocalDateTime getIssuedAt() {
            if (iat != null) {
                return LocalDateTime.ofInstant(new Date(iat * 1000).toInstant(), ZoneId.systemDefault());
            }
            return null;
        }

        public String getUsername() {
            return sub;
        }

        public boolean hasRole(String name) {
            return scopes != null ? scopes.stream()
                    .filter(s -> s.equalsIgnoreCase(name))
                    .findFirst().isPresent() :
                    false;
        }
    }
}
