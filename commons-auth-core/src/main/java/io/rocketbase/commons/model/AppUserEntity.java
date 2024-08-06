package io.rocketbase.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public interface AppUserEntity extends UserDetails, AppUserToken, EntityWithKeyValue<AppUserEntity> {

    void setId(String id);

    void setUsername(@NotNull String username);

    String getPassword();

    void setPassword(@NotNull String password);

    void setFirstName(String firstName);
    
    void setLastName(String lastName);

    void setEmail(@NotNull @Email String email);

    void setAvatar(String avatar);

    void setRoles(List<String> roles);

    void setEnabled(boolean enabled);

    Instant getCreated();

    /**
     * @return null or date of last login with TimeZone UTC
     */
    Instant getLastLogin();

    /**
     * used for analytics of inactive user xyz<br>
     * should update lastLogin with TimeZone UTC
     */
    void updateLastLogin();

    /**
     * @return null or date of last token invalidation with TimeZone UTC
     */
    Instant getLastTokenInvalidation();

    /**
     * used to mark token that are issued before as invalid<br>
     * should update lastTokenInvalidation with TimeZone UTC
     */
    void updateLastTokenInvalidation();


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
