package io.rocketbase.commons.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * combines AppUserEntity + AppUserToken to implement {@link UserDetails}<br>
 * token holds combined capabilities (authorities) and appUser hold password, enabled...
 */
@Getter
@RequiredArgsConstructor
public class AppUserTokenDetails implements UserDetails {

    private final AppUserEntity appUser;
    private final AppUserToken appUserToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return appUserToken.getCapabilities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return appUser.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return appUser.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !appUser.isLocked();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
