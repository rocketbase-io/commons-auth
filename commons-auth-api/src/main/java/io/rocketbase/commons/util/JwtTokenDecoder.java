package io.rocketbase.commons.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Base64;
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

        @Getter
        @JsonProperty("user_id")
        private String userId;
        /**
         * roles
         */
        private List<String> scopes;

        public boolean isExpired() {
            if (exp != null) {
                return getExpiration().isBefore(Instant.now());
            }
            return false;
        }

        public Instant getExpiration() {
            if (exp != null) {
                return Instant.ofEpochSecond(exp, 0);
            }
            return null;
        }

        public Instant getIssuedAt() {
            if (iat != null) {
                return Instant.ofEpochSecond(iat, 0);
            }
            return null;
        }

        public String getUsername() {
            return sub;
        }

        public boolean hasRole(String name) {
            return scopes != null && scopes.stream()
                    .filter(s -> s.equalsIgnoreCase(name))
                    .findFirst().isPresent();
        }
    }
}
