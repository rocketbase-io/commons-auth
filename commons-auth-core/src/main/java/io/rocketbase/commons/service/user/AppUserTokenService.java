package io.rocketbase.commons.service.user;

import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

/**
 * transpiles a AppUserEntity to AppTokenReference<br>
 * lookups their groups, teams and combined capabilities and keyValues
 */
public interface AppUserTokenService extends UserDetailsService {

    Optional<AppUserToken> findByUsername(String username);

    Optional<AppUserToken> findByEmail(String email);

    Optional<AppUserToken> findById(String id);

    default Optional<AppUserToken> findByIdOrUsername(String idOrUsername) {
        Optional<AppUserToken> optional = findById(idOrUsername);
        if (!optional.isPresent()) {
            optional = findByUsername(idOrUsername);
        }
        return optional;
    }

    AppUserToken lookup(AppUserEntity appUser);
}
