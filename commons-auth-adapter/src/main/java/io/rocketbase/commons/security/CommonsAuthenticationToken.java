package io.rocketbase.commons.security;

import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CommonsAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final AppUser principal;

    @Getter
    private final JwtTokenStore jwtTokenStore;

    public CommonsAuthenticationToken(Collection<? extends GrantedAuthority> authorities, AppUser principal, JwtTokenStore jwtTokenStore) {
        super(authorities);
        this.principal = principal;
        this.jwtTokenStore = jwtTokenStore;
    }

    @Override
    public Object getCredentials() {
        return jwtTokenStore.getTokenBundle();
    }

}
