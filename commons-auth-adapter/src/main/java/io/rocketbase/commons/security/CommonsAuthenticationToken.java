package io.rocketbase.commons.security;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;

public class CommonsAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final CommonsPrincipal principal;

    @Getter
    private final JwtTokenStore jwtTokenStore;

    @Getter
    @Setter
    private WebAuthenticationDetails details;

    public CommonsAuthenticationToken(Collection<? extends GrantedAuthority> authorities, AppUserRead principal, JwtTokenStore jwtTokenStore) {
        super(authorities);
        this.principal = new CommonsPrincipal(principal);
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
