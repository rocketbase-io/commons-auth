package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rocketbase.commons.dto.appuser.AppUserReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public abstract String getAvatar();

    public abstract void setAvatar(String avatar);

    public abstract List<String> getRoles();

    public abstract void setRoles(List<String> roles);

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean enabled);

    public abstract LocalDateTime getCreated();

    /**
     * @return null or date of last login with TimeZone UTC
     */
    public abstract LocalDateTime getLastLogin();

    /**
     * used for analytics of inactive user xyz<br>
     * should update lastLogin with TimeZone UTC
     */
    public abstract void updateLastLogin();

    /**
     * @return null or date of last token invalidation with TimeZone UTC
     */
    public abstract LocalDateTime getLastTokenInvalidation();

    /**
     * used to mark token that are issued before as invalid<br>
     * should update lastTokenInvalidation with TimeZone UTC
     */
    public abstract void updateLastTokenInvalidation();

    /**
     * @param key   will get stored with lowercase<br>
     *              max length of 50 characters<br>
     *              key with _ as prefix will not get displayed in REST_API
     * @param value max length of 4000 characters
     * @return itself for fluent api
     */
    public abstract AppUser addKeyValue(String key, String value);

    public abstract void removeKeyValue(String key);

    public abstract boolean hasKeyValue(String key);

    public abstract String getKeyValue(String key);

    /**
     * @return an immutable map so that changes should only be done by add/remove KeyValue
     */
    public abstract Map<String, String> getKeyValues();

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

    protected void checkKeyValue(String key, String value) {
        Assert.hasLength(key, "Key must not be empty");
        Assert.state(key.length() <= 50, "Key is too long - at least 50 chars");
        Assert.hasLength(value, "Value must not be empty");
        Assert.state(value.length() <= 4000, "Value is too long - at least 4000 chars");
    }

    /**
     * convert current instance to a simple reference copy
     *
     * @return fresh created reference based on this {@link AppUser}
     */
    @JsonIgnore
    public AppUserReference toReference() {
        return AppUserReference.builder()
                .id(getId())
                .username(getUsername())
                .firstName(getFirstName())
                .lastName(getLastName())
                .email(getEmail())
                .avatar(getAvatar())
                .build();
    }

    /**
     * combines first + last name
     */
    @JsonIgnore
    public String getFullName() {
        boolean emptyFirstName = StringUtils.isEmpty(getFirstName());
        boolean emptyLastName = StringUtils.isEmpty(getLastName());
        if (emptyFirstName && emptyLastName) {
            return null;
        } else if (!emptyFirstName && !emptyLastName) {
            return String.format("%s %s", getFirstName(), getLastName());
        } else if (!emptyFirstName) {
            return getFirstName();
        } else {
            return getLastName();
        }
    }

    /**
     * fullname fallback if null use username
     */
    @JsonIgnore
    public String getDisplayName() {
        String fullName = getFullName();
        if (fullName == null) {
            return getUsername();
        }
        return fullName;
    }
}
