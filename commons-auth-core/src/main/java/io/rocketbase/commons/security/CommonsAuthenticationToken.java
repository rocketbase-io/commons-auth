package io.rocketbase.commons.security;

import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CommonsAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final CommonsPrincipal principal;

    @Getter
    private final JwtTokenStore jwtTokenStore;

    public CommonsAuthenticationToken(Collection<? extends GrantedAuthority> authorities, AppUserToken appUserToken, JwtTokenStore jwtTokenStore) {
        super(authorities);
        this.principal = new CommonsPrincipal(appUserToken);
        this.jwtTokenStore = jwtTokenStore;
    }

    @Override
    public Object getCredentials() {
        return jwtTokenStore.getTokenBundle();
    }

    /**
     * shortcut for username of token
     */
    public String getUsername() {
        return principal.getUsername();
    }

}
