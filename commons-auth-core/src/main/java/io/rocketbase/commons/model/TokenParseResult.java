package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenParseResult {

    private String token;

    private AppUserToken user;

    private Instant issuedAt;

    private Instant expiration;

    public Set<GrantedAuthority> getAuthoritiesFromToken() {
        if (user != null && user.getCapabilities() != null) {
            return user.getCapabilities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

}
