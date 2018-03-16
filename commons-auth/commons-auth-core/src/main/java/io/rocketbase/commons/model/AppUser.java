package io.rocketbase.commons.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AppUser implements UserDetails {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getUsername();

    public abstract void setUsername(String username);

    public abstract String getPassword();

    public abstract void setPassword(String password);

    public abstract String getFirstName();

    public abstract void setFirstName(String firstName);

    public abstract String getLastName();

    public abstract void setLastName(String lastName);

    public abstract String getEmail();

    public abstract void setEmail(String email);

    public abstract List<String> getRoles();

    public abstract void setRoles(List<String> roles);

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean enabled);

    public abstract LocalDateTime getCreated();

    public abstract LocalDateTime getLastLogin();

    public abstract void updateLastLogin();

    public abstract LocalDateTime getLastTokenInvalidation();

    public abstract void updateLastTokenInvalidation();


    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles() != null ?
                getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(String.format("ROLE_%s", r)))
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }
}
