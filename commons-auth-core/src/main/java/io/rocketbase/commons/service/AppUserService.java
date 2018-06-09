package io.rocketbase.commons.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    public static String REGISTRATION_KV = "_registration";
    public static String FORGOTPW_KV = "_forgotpw";

    final AuthProperties authProperties;
    final RegistrationProperties registrationProperties;

    @Resource
    private AppUserPersistenceService appUserPersistenceService;

    @Resource
    private GravatarService gravatarService;

    @Resource
    private PasswordEncoder passwordEncoder;

    private LoadingCache<String, Optional<AppUser>> cache;

    @PostConstruct
    public void postConstruct() {
        if (authProperties.getUserCacheTime() > 0) {
            cache = CacheBuilder.newBuilder()
                    .expireAfterAccess(authProperties.getUserCacheTime(), TimeUnit.MINUTES)
                    .build(new CacheLoader<String, Optional<AppUser>>() {
                        @Override
                        public Optional<AppUser> load(String key) {
                            return appUserPersistenceService.findByUsername(key);
                        }
                    });
        }
    }

    @SneakyThrows
    public AppUser getByUsername(String username) {
        Optional<AppUser> userEntity = null;
        if (cache != null) {
            userEntity = cache.get(username);
        } else {
            userEntity = appUserPersistenceService.findByUsername(username);
        }
        if (userEntity.isPresent()) {
            return userEntity.get();
        }
        return null;
    }

    public Optional<AppUser> findByEmail(String email) {
        return appUserPersistenceService.findByEmail(email);
    }

    public AppUser updateLastLogin(String username) {
        AppUser entity = getEntityByUsername(username);
        entity.updateLastLogin();
        appUserPersistenceService.save(entity);
        refreshUsername(username);
        return entity;
    }

    public void updatePassword(String username, String newPassword) {
        AppUser entity = getEntityByUsername(username);
        entity.setPassword(passwordEncoder.encode(newPassword));
        entity.updateLastTokenInvalidation();

        appUserPersistenceService.save(entity);
        refreshUsername(username);
    }

    public void updateProfile(String username, String firstName, String lastName, String avatar, Map<String, String> keyValues) {
        AppUser entity = getEntityByUsername(username);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setAvatar(avatar);
        handleKeyValues(entity, keyValues);

        appUserPersistenceService.save(entity);
        refreshUsername(username);
    }

    public void updateKeyValues(String username, Map<String, String> keyValues) {
        AppUser entity = getEntityByUsername(username);
        handleKeyValues(entity, keyValues);

        appUserPersistenceService.save(entity);
        refreshUsername(username);
    }

    @Nonnull
    private AppUser getEntityByUsername(String username) {
        Optional<AppUser> optional = appUserPersistenceService.findByUsername(username);
        if (!optional.isPresent()) {
            throw new NotFoundException();
        }
        return optional.get();
    }

    public void refreshUsername(String username) {
        if (cache != null) {
            cache.invalidate(username);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser entity = getByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException(String.format("user: %s not found", username));
        }
        return entity;
    }

    public AppUser initializeUserIfNotExists(String username, String password, String email, boolean admin) {
        AppUser result = getByUsername(username);
        if (result == null) {
            result = initializeUser(username, password, email, admin);
        }
        return result;
    }

    public AppUser initializeUser(String username, String password, String email, boolean admin) {
        AppUser instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(username.toLowerCase());
        instance.setEmail(email.toLowerCase());
        instance.setPassword(passwordEncoder.encode(password));
        instance.setRoles(Arrays.asList(admin ? authProperties.getRoleAdmin() : authProperties.getRoleUser()));
        instance.setEnabled(true);
        if (gravatarService.isEnabled()) {
            instance.setAvatar(gravatarService.getAvatar(email));
        }

        AppUser entity = appUserPersistenceService.save(instance);
        refreshUsername(entity.getUsername());
        return entity;
    }

    public AppUser registerUser(RegistrationRequest registration) {
        AppUser instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(registration.getUsername().toLowerCase());
        instance.setEmail(registration.getEmail().toLowerCase());
        instance.setFirstName(registration.getFirstName());
        instance.setLastName(registration.getLastName());
        instance.setPassword(passwordEncoder.encode(registration.getPassword()));
        instance.setRoles(Arrays.asList(registrationProperties.getRole()));
        instance.setEnabled(!registrationProperties.isVerification());
        if (gravatarService.isEnabled()) {
            instance.setAvatar(gravatarService.getAvatar(registration.getEmail()));
        }
        handleKeyValues(instance, registration.getKeyValues());

        AppUser entity = appUserPersistenceService.save(instance);
        refreshUsername(entity.getUsername());
        return entity;
    }

    public void handleKeyValues(AppUser user, Map<String, String> keyValues) {
        if (keyValues != null) {
            keyValues.forEach((key, value) -> {
                if (value != null) {
                    user.addKeyValue(key, value);
                } else {
                    user.removeKeyValue(key);
                }
            });
        }
    }

    public void processRegistrationVerification(String username) {
        AppUser entity = getByUsername(username);
        if (entity == null) {
            throw new NotFoundException();
        }

        entity.setEnabled(true);
        entity.updateLastLogin();

        handleKeyValues(entity, ImmutableMap.of(REGISTRATION_KV, null));

        appUserPersistenceService.save(entity);
        refreshUsername(entity.getUsername());
    }

    public void delete(AppUser user) {
        appUserPersistenceService.delete(user);
        refreshUsername(user.getUsername());
    }
}
