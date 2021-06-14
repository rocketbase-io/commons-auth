package io.rocketbase.commons.security;

import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.util.JwtTokenStore;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * within the security context the Authentication should be an instance of {@link CommonsAuthenticationToken} and it's principal is an instance of {@link CommonsPrincipal}<br>
 * this class provides some convenient static helps that simply the handling of the {@link SecurityContextHolder}
 */
@NoArgsConstructor
public class CommonsPrincipal implements AppUserToken, Principal {

    private AppUserToken user;

    public CommonsPrincipal(AppUserToken user) {
        this.user = user;
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
            return current.getKeyValues() != null ? current.getKeyValues().getOrDefault(key, null) : null;
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
    public String getId() {
        return user != null ? user.getId() : null;
    }

    @Override
    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    @Override
    public String getFirstName() {
        return user != null ? user.getFirstName() : null;
    }

    @Override
    public String getLastName() {
        return user != null ? user.getLastName() : null;
    }

    @Override
    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    @Override
    public String getAvatar() {
        return user != null ? user.getAvatar() : null;
    }

    @Override
    public List<String> getRoles() {
        return user != null ? user.getRoles() : null;
    }

    @Override
    public Map<String, String> getKeyValues() {
        return user != null ? user.getKeyValues() : null;
    }

    @Override
    public void setKeyValues(Map<String, String> map) {
        if (this.user != null) {
            this.user.setKeyValues(map);
        }
    }

    @Override
    public String getName() {
        return getUsername();
    }

}
