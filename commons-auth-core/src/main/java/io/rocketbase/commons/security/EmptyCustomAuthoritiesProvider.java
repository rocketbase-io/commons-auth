package io.rocketbase.commons.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * default implementation of {@link CustomAuthoritiesProvider}
 */
public class EmptyCustomAuthoritiesProvider implements CustomAuthoritiesProvider {

    @Override
    public Collection<? extends GrantedAuthority> getExtraTokenAuthorities(String username) {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getExtraSecurityContextAuthorities(String username) {
        return Collections.emptyList();
    }
}
