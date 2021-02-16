package io.rocketbase.commons.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class CapacityAuthoritiesConverter {

    /**
     * converts a list of roles into a collection of GrantedAuthority
     *
     * @return never null
     */
    public static Collection<? extends GrantedAuthority> convert(Collection<String> roles) {
        return roles != null ?
                roles.stream()
                        .map(r -> new SimpleGrantedAuthority(r))
                        .collect(Collectors.toList()) :
                new HashSet<>();
    }

    /**
     * converts a collection of authorities into a list of roles
     *
     * @return never null
     */
    public static Set<String> convertToDtos(Collection<? extends GrantedAuthority> authorities) {
        return authorities != null ?
                authorities.stream()
                        .filter(r -> r.getAuthority() != null)
                        .map(r -> r.getAuthority())
                        .collect(Collectors.toSet()) :
                new HashSet<>();
    }
}
