package io.rocketbase.commons.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rocketbase.commons.config.AuthConfiguration;
import io.rocketbase.commons.config.GravatarConfiguration;
import io.rocketbase.commons.config.RegistrationConfiguration;
import io.rocketbase.commons.dto.RegistrationRequest;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUser;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AppUserService implements UserDetailsService {

    @Resource
    private AuthConfiguration authConfiguration;

    @Resource
    private RegistrationConfiguration registrationConfiguration;

    @Resource
    private GravatarConfiguration gravatarConfiguration;

    @Resource
    private AppUserPersistenceService appUserPersistenceService;

    @Resource
    private GravatarService gravatarService;

    @Resource
    private PasswordEncoder passwordEncoder;

    private LoadingCache<String, Optional<AppUser>> cache;

    @PostConstruct
    public void postConstruct() {
        if (authConfiguration.getUserCacheTime() > 0) {
            cache = CacheBuilder.newBuilder()
                    .expireAfterAccess(authConfiguration.getUserCacheTime(), TimeUnit.MINUTES)
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

    @Nonnull
    private AppUser getEntityByUsername(String username) {
        Optional<AppUser> optional = appUserPersistenceService.findByUsername(username);
        if (!optional.isPresent()) {
            throw new NotFoundException();
        }
        return optional.get();
    }

    private void refreshUsername(String username) {
        if (cache != null) {
            cache.refresh(username);
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

    public AppUser initializeUser(String username, String password, String email, boolean admin) {
        AppUser instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(username.toLowerCase());
        instance.setEmail(email.toLowerCase());
        instance.setPassword(passwordEncoder.encode(password));
        instance.setRoles(Arrays.asList(admin ? authConfiguration.getRoleNameAdmin() : authConfiguration.getRoleNameUser()));
        instance.setEnabled(true);
        if (gravatarConfiguration.isEnabled()) {
            instance.setAvatar(gravatarService.getAvatar(email));
        }

        return appUserPersistenceService.save(instance);
    }

    public AppUser registerUser(RegistrationRequest registration) {
        AppUser instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(registration.getUsername().toLowerCase());
        instance.setEmail(registration.getEmail().toLowerCase());
        instance.setFirstName(registration.getFirstName());
        instance.setLastName(registration.getLastName());
        instance.setPassword(passwordEncoder.encode(registration.getPassword()));
        instance.setRoles(Arrays.asList(registrationConfiguration.getRole()));
        instance.setEnabled(!registrationConfiguration.isEmailValidation());
        if (gravatarConfiguration.isEnabled()) {
            instance.setAvatar(gravatarService.getAvatar(registration.getEmail()));
        }

        return appUserPersistenceService.save(instance);
    }

    public AppUser registrationVerification(String username) {
        AppUser entity = getByUsername(username);
        if (entity == null) {
            throw new NotFoundException();
        }

        entity.setEnabled(true);
        entity.updateLastLogin();

        return appUserPersistenceService.save(entity);
    }

    public void delete(AppUser user) {
        appUserPersistenceService.delete(user);
    }
}
