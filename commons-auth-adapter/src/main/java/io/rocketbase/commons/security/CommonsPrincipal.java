package io.rocketbase.commons.security;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

/**
 * within the security context the Authentication should be an instance of {@link CommonsAuthenticationToken} and it's principal is an instance of {@link CommonsPrincipal}<br>
 * this class provides some convenient static helps that simply the handling of the {@link SecurityContextHolder}
 */
@NoArgsConstructor
public class CommonsPrincipal extends AppUserRead implements Principal {

    public CommonsPrincipal(AppUserRead user) {
        super(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getAvatar(), user.getRoles(), user.getKeyValues(), user.isEnabled(), user.getCreated(), user.getLastLogin());
    }

    /**
     * @return the <code>CommonsPrincipal</code> or <code>null</code> if no authentication
     * * information is available
     */
    public static CommonsPrincipal getCurrent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CommonsAuthenticationToken) {
            return ((CommonsAuthenticationToken) authentication).getPrincipal();
        }
        return null;
    }

    /**
     * @param key is stored internally in lowercase so that this function also lowers the given key...
     * @return the value of the given key when user is logged in and key exists<br>
     */
    public static String getCurrentKeyValue(String key) {
        CommonsPrincipal current = getCurrent();
        if (current != null) {
            return current.getKeyValues() != null ? current.getKeyValues().getOrDefault(key.toLowerCase(), null) : null;
        }
        return null;
    }

    /**
     * @return {@link JwtTokenStore} or null in case of not authentication
     */
    public static JwtTokenStore getCurrentJwtTokenStore() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CommonsAuthenticationToken) {
            return ((CommonsAuthenticationToken) authentication).getJwtTokenStore();
        }
        return null;
    }

    /**
     * check if {@link JwtTokenStore} is available and perform's refresh if needed (not valid for at least 180sec)
     *
     * @return null in case no token exisits
     */
    public static String getValidJwtToken() {
        JwtTokenStore store = getCurrentJwtTokenStore();
        if (store != null) {
            if (store.checkTokenNeedsRefresh(180)) {
                store.refreshToken();
            }
            return store.getTokenBundle().getToken();
        }
        return null;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}
