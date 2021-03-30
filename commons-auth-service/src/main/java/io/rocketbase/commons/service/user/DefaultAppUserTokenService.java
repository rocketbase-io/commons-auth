package io.rocketbase.commons.service.user;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.AppUserTokenDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
public class DefaultAppUserTokenService implements AppUserTokenService {

    private final AppUserService appUserService;
    private final AppUserConverter appUserConverter;

    /**
     * lookup user also via email-adress if used...
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserEntity entity = appUserService.getByUsername(username);
        if (entity == null) {
            entity = appUserService.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format("user: %s not found", username)));
        }
        return new AppUserTokenDetails(entity, lookup(entity));
    }

    /**
     * resolved keyValues and capabilities<br>
     * keyValues of: group, user activeTeam<br>
     * capabilities of: user and group
     */
    @Override
    public Optional<AppUserToken> findByUsername(String username) {
        return convertOptional(appUserService.findByIdOrUsername(username));
    }

    @Override
    public Optional<AppUserToken> findByEmail(String email) {
        return convertOptional(appUserService.findByEmail(email));
    }

    @Override
    public Optional<AppUserToken> findById(String id) {
        return convertOptional(appUserService.findByIdOrUsername(id));
    }

    @Override
    public AppUserToken lookup(AppUserEntity appUser) {
        return appUserConverter.toToken(appUser);
    }

    protected Optional<AppUserToken> convertOptional(Optional<AppUserEntity> optional) {
        return optional.isPresent() ? Optional.of(appUserConverter.toToken(optional.get())) : Optional.empty();
    }
}
