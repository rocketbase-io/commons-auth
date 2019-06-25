package io.rocketbase.commons.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class RolesAuthoritiesConverter {

    /**
     * converts a list of roles into a collection of GrantedAuthority
     *
     * @return never null
     */
    public static Collection<? extends GrantedAuthority> convert(List<String> roles) {
        return roles != null ?
                roles.stream()
                        .map(r -> new SimpleGrantedAuthority(String.format("ROLE_%s", r.replaceAll("^ROLE_", ""))))
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    /**
     * converts a collection of authorities into a list of roles
     *
     * @return never null
     */
    public static List<String> convert(Collection<? extends GrantedAuthority> authorities) {
        return authorities != null ?
                authorities.stream()
                        .filter(r -> r.getAuthority() != null)
                        .map(r -> r.getAuthority().replaceAll("^ROLE_", ""))
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }
}
