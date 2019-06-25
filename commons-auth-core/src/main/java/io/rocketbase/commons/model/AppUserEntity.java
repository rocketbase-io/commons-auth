package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

public interface AppUserEntity extends UserDetails, AppUserToken {

    void setId(String id);

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);


    void setFirstName(String firstName);


    void setLastName(String lastName);

    void setEmail(String email);

    void setAvatar(String avatar);

    void setRoles(List<String> roles);

    void setEnabled(boolean enabled);

    LocalDateTime getCreated();

    /**
     * @return null or date of last login with TimeZone UTC
     */
    LocalDateTime getLastLogin();

    /**
     * used for analytics of inactive user xyz<br>
     * should update lastLogin with TimeZone UTC
     */
    void updateLastLogin();

    /**
     * @return null or date of last token invalidation with TimeZone UTC
     */
    LocalDateTime getLastTokenInvalidation();

    /**
     * used to mark token that are issued before as invalid<br>
     * should update lastTokenInvalidation with TimeZone UTC
     */
    void updateLastTokenInvalidation();

    /**
     * @param key   will get stored with lowercase<br>
     *              max length of 50 characters<br>
     *              key with _ as prefix will not get displayed in REST_API
     * @param value max length of 4000 characters
     * @return itself for fluent api
     */
    default AppUserEntity addKeyValue(String key, String value) {
        checkKeyValue(key, value);
        getKeyValues().put(key.toLowerCase(), value);
        return this;
    }

    default void removeKeyValue(String key) {
        getKeyValues().remove(key.toLowerCase());
    }

    default void checkKeyValue(String key, String value) {
        Assert.hasLength(key, "Key must not be empty");
        Assert.state(key.length() <= 50, "Key is too long - at least 50 chars");
        Assert.hasLength(value, "Value must not be empty");
        Assert.state(value.length() <= 4000, "Value is too long - at least 4000 chars");
    }

    /**
     * convert current instance to a simple reference copy
     *
     * @return fresh created reference based on this {@link AppUserEntity}
     */
    @JsonIgnore
    default AppUserReference toReference() {
        return SimpleAppUserReference.builder()
                .id(getId())
                .username(getUsername())
                .firstName(getFirstName())
                .lastName(getLastName())
                .email(getEmail())
                .avatar(getAvatar())
                .build();
    }


    @JsonIgnore
    default boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    default boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    default boolean isCredentialsNonExpired() {
        return true;
    }
}
