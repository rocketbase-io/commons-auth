package io.rocketbase.commons.security;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

@NoArgsConstructor
public class CommonsPrincipal extends AppUserRead implements Principal {

    public CommonsPrincipal(AppUserRead user) {
        super(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getAvatar(), user.getRoles(), user.getKeyValues(), user.getEnabled(), user.getCreated(), user.getLastLogin());
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

    @Override
    public String getName() {
        return getUsername();
    }
}