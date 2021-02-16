package io.rocketbase.commons.service.user;

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
    /*
    protected AppUserToken toToken() {
        Map<String, String> keyValues = new HashMap<>();
        // first read all
        if (getGroups() != null) {
            List<AppGroupRead> sortedGroups = getGroups().stream()
                    .filter(g -> getKeyValues() != null)
                    .sorted(Comparator
                            .comparing(AppGroupRead::getDepth)
                            .thenComparing(AppGroupRead::getName))
                    .collect(Collectors.toList());
            for (AppGroupRead g : sortedGroups) {
                keyValues.putAll(g.getKeyValues());
            }
        }
        if (getKeyValues() != null) {
            keyValues.putAll(getKeyValues());
        }

        if (getActiveTeam() != null && getActiveTeam().getTeam().getKeyValues() != null) {
            keyValues.putAll(getActiveTeam().getTeam().getKeyValues());
        }

        return SimpleAppUserToken.builderToken()
                .id(getId())
                .systemRefId(getSystemRefId())
                .username(getUsername())
                .email(getEmail())
                .profile(getProfile())
                .groups(getGroups() != null ? getGroups().stream().map(AppGroupRead::toShort).collect(Collectors.toSet()) : null)
                .capabilities(getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                .build();
    }
     */
    @Override
    public Optional<AppUserToken> findByUsername(String username) {
        return null;
    }

    @Override
    public Optional<AppUserToken> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<AppUserToken> findById(String id) {
        return Optional.empty();
    }

    @Override
    public AppUserToken lookup(AppUserEntity appUser) {
        return null;
    }
}
