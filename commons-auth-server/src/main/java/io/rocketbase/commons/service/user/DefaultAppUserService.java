package io.rocketbase.commons.service.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.AppUserPersistenceService;
import io.rocketbase.commons.service.avatar.AvatarService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class DefaultAppUserService implements AppUserService {

    public static String REGISTRATION_KV = "_registration";
    public static String FORGOTPW_KV = "_forgotpw";

    final AuthProperties authProperties;
    final RegistrationProperties registrationProperties;

    @Resource
    protected AppUserPersistenceService<AppUserEntity> appUserPersistenceService;

    @Resource
    protected AvatarService avatarService;

    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected ValidationService validationService;

    protected LoadingCache<CacheFilter, Optional<AppUserEntity>> cache;

    @PostConstruct
    public void postConstruct() {
        if (authProperties.getUserCacheTime() > 0) {
            cache = CacheBuilder.newBuilder()
                    .expireAfterAccess(authProperties.getUserCacheTime(), TimeUnit.MINUTES)
                    .build(new CacheLoader<CacheFilter, Optional<AppUserEntity>>() {
                        @Override
                        public Optional<AppUserEntity> load(CacheFilter key) {
                            if (key.getUsername() != null) {
                                return appUserPersistenceService.findByUsername(key.getUsername());
                            } else if (key.getEmail() != null) {
                                return appUserPersistenceService.findByEmail(key.getEmail());
                            } else {
                                return appUserPersistenceService.findById(key.getId());
                            }
                        }
                    });
        }
    }

    @Override
    public AppUserEntity getByUsername(String username) {
        Optional<AppUserEntity> userEntity;
        if (cache != null) {
            userEntity = cache.getUnchecked(CacheFilter.builder().username(username).build());
        } else {
            userEntity = appUserPersistenceService.findByUsername(username);
        }
        return userEntity.orElse(null);
    }

    @Override
    public Optional<AppUserEntity> findByEmail(String email) {
        if (cache != null) {
            return cache.getUnchecked(CacheFilter.builder().email(email.toLowerCase()).build());
        } else {
            return appUserPersistenceService.findByEmail(email.toLowerCase());
        }
    }

    @Override
    public Optional<AppUserEntity> findById(String id) {
        if (cache != null) {
            return cache.getUnchecked(CacheFilter.builder().id(id).build());
        } else {
            return appUserPersistenceService.findById(id);
        }
    }

    @Override
    public AppUserEntity updateLastLogin(String username) {
        AppUserEntity entity = getEntityByUsername(username);
        entity.updateLastLogin();
        appUserPersistenceService.save(entity);
        return refreshUsername(username);
    }

    @Override
    public AppUserEntity updatePassword(String username, String newPassword) {
        validationService.passwordIsValid(newPassword);

        AppUserEntity entity = getEntityByUsername(username);
        entity.setPassword(passwordEncoder.encode(newPassword));
        entity.updateLastTokenInvalidation();

        appUserPersistenceService.save(entity);
        return refreshUsername(username);
    }

    @Override
    public AppUserEntity updateProfile(String username, String firstName, String lastName, String avatar, Map<String, String> keyValues) {
        AppUserEntity entity = getEntityByUsername(username);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setAvatar(avatar);
        handleKeyValues(entity, keyValues);

        appUserPersistenceService.save(entity);
        return refreshUsername(username);
    }

    @Override
    public AppUserEntity updateKeyValues(String username, Map<String, String> keyValues) {
        AppUserEntity entity = getEntityByUsername(username);
        handleKeyValues(entity, keyValues);

        appUserPersistenceService.save(entity);
        return refreshUsername(username);
    }

    private AppUserEntity getEntityByUsername(String username) {
        Optional<AppUserEntity> optional = appUserPersistenceService.findByUsername(username);
        if (!optional.isPresent()) {
            throw new NotFoundException();
        }
        return optional.get();
    }

    @Override
    public AppUserEntity refreshUsername(String username) {
        if (cache != null) {
            cache.invalidate(username);
        }
        return getByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserEntity entity = getByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException(String.format("user: %s not found", username));
        }
        return entity;
    }

    @Override
    public AppUserEntity initializeUserIfNotExists(String username, String password, String email, boolean admin) {
        AppUserEntity result = getByUsername(username);
        if (result == null) {
            result = initializeUser(username, password, email, admin);
        }
        return result;
    }

    @Override
    public AppUserEntity initializeUser(String username, String password, String email, boolean admin) throws UsernameNotFoundException, EmailValidationException {
        return initializeUser(username, password, email, Collections.singletonList(admin ? authProperties.getRoleAdmin() : authProperties.getRoleUser()));
    }

    @Override
    public AppUserEntity initializeUser(String username, String password, String email, List<String> roles) throws UsernameNotFoundException, EmailValidationException {
        validationService.usernameIsValid(username);
        validationService.emailIsValid(email);

        AppUserEntity instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(username.toLowerCase());
        instance.setEmail(email.toLowerCase());
        instance.setPassword(passwordEncoder.encode(password));
        instance.setRoles(roles);
        instance.setEnabled(true);
        if (avatarService.isEnabled()) {
            instance.setAvatar(avatarService.getAvatar(email));
        }
        AppUserEntity entity = appUserPersistenceService.save(instance);
        return refreshUsername(entity.getUsername());
    }

    @Override
    public AppUserEntity updateRoles(String username, List<String> roles) {
        AppUserEntity entity = getEntityByUsername(username);
        entity.setRoles(roles);
        entity.updateLastTokenInvalidation();
        appUserPersistenceService.save(entity);
        return refreshUsername(username);
    }

    @Override
    public AppUserEntity registerUser(RegistrationRequest registration) {
        validationService.validateRegistration(registration.getUsername(), registration.getPassword(), registration.getEmail());

        AppUserEntity instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(registration.getUsername().toLowerCase());
        instance.setEmail(registration.getEmail().toLowerCase());
        instance.setFirstName(registration.getFirstName());
        instance.setLastName(registration.getLastName());
        instance.setPassword(passwordEncoder.encode(registration.getPassword()));
        instance.setRoles(Collections.singletonList(registrationProperties.getRole()));
        instance.setEnabled(!registrationProperties.isVerification());
        if (avatarService.isEnabled()) {
            instance.setAvatar(avatarService.getAvatar(registration.getEmail()));
        }
        handleKeyValues(instance, registration.getKeyValues());

        AppUserEntity entity = appUserPersistenceService.save(instance);
        return refreshUsername(entity.getUsername());
    }

    @Override
    public void handleKeyValues(AppUserEntity user, Map<String, String> keyValues) {
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

    @Override
    public void processRegistrationVerification(String username) {
        AppUserEntity entity = getByUsername(username);
        if (entity == null) {
            throw new NotFoundException();
        }

        entity.setEnabled(true);
        entity.updateLastLogin();

        // remove registration Key from keyValue map
        HashMap<String, String> keyValues = new HashMap<>();
        keyValues.put(REGISTRATION_KV, null);
        handleKeyValues(entity, keyValues);

        appUserPersistenceService.save(entity);
        refreshUsername(entity.getUsername());
    }

    @Override
    public void delete(AppUserEntity user) {
        appUserPersistenceService.delete(user);
        refreshUsername(user.getUsername());
    }

    @Builder
    @Data
    private static class CacheFilter {
        private String username;
        private String email;
        private String id;
    }
}
