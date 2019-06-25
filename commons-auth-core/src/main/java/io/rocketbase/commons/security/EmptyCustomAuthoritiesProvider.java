package io.rocketbase.commons.security;

import io.rocketbase.commons.model.AppUserToken;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;

/**
 * default implementation of {@link CustomAuthoritiesProvider}
 */
public class EmptyCustomAuthoritiesProvider implements CustomAuthoritiesProvider {

    @Override
    public Collection<? extends GrantedAuthority> getExtraTokenAuthorities(AppUserToken user) {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getExtraSecurityContextAuthorities(AppUserToken user, HttpServletRequest request) {
        return Collections.emptyList();
    }
}
