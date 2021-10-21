package io.rocketbase.commons.security;

import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CommonsAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final CommonsPrincipal principal;

    @Getter
    private final JwtTokenStore jwtTokenStore;

    @Nullable
    @Getter
    private final Long clientId;

    public CommonsAuthenticationToken(Collection<? extends GrantedAuthority> authorities, AppUserToken appUserToken, JwtTokenStore jwtTokenStore, @Nullable Long clientId) {
        super(authorities);
        this.principal = new CommonsPrincipal(appUserToken);
        this.jwtTokenStore = jwtTokenStore;
        this.clientId = clientId;
    }

    @Override
    public Object getCredentials() {
        return jwtTokenStore.getTokenBundle();
    }

    public Set<String> getCapabilities() {
        return getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    /**
     * shortcut for username of token
     */
    public String getUsername() {
        return principal.getUsername();
    }

    /**
     * shortcut for id of token
     */
    public String getId() {
        return principal.getId();
    }

}
