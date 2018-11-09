package io.rocketbase.commons.security;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import lombok.NoArgsConstructor;

import java.security.Principal;

@NoArgsConstructor
public class CommonsPrincipal extends AppUserRead implements Principal {

    public CommonsPrincipal(AppUserRead user) {
        super(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getAvatar(), user.getRoles(), user.getKeyValues(), user.getEnabled(), user.getCreated(), user.getLastLogin());
    }

    @Override
    public String getName() {
        return getUsername();
    }
}
