package io.rocketbase.commons.service.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.AppUserPersistenceService;
import io.rocketbase.commons.service.avatar.AvatarService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
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
    protected AppUserPersistenceService appUserPersistenceService;

    @Resource
    protected AvatarService avatarService;

    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected ValidationService validationService;

    protected LoadingCache<String, Optional<AppUser>> cache;

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

    @Override
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

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return appUserPersistenceService.findByEmail(email.toLowerCase());
    }

    @Override
    public Optional<AppUser> findById(String id) {
        return appUserPersistenceService.findById(id);
    }

    @Override
    public AppUser updateLastLogin(String username) {
        AppUser entity = getEntityByUsername(username);
        entity.updateLastLogin();
        appUserPersistenceService.save(entity);
        refreshUsername(username);
        return getByUsername(username);
    }

    @Override
    public AppUser updatePassword(String username, String newPassword) {
        validationService.passwordIsValid(newPassword);

        AppUser entity = getEntityByUsername(username);
        entity.setPassword(passwordEncoder.encode(newPassword));
        entity.updateLastTokenInvalidation();

        appUserPersistenceService.save(entity);
        return refreshUsername(username);
    }

    @Override
    public AppUser updateProfile(String username, String firstName, String lastName, String avatar, Map<String, String> keyValues) {
        AppUser entity = getEntityByUsername(username);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setAvatar(avatar);
        handleKeyValues(entity, keyValues);

        appUserPersistenceService.save(entity);
        return refreshUsername(username);
    }

    @Override
    public AppUser updateKeyValues(String username, Map<String, String> keyValues) {
        AppUser entity = getEntityByUsername(username);
        handleKeyValues(entity, keyValues);

        appUserPersistenceService.save(entity);
        return refreshUsername(username);
    }

    @Nonnull
    protected AppUser getEntityByUsername(String username) {
        Optional<AppUser> optional = appUserPersistenceService.findByUsername(username);
        if (!optional.isPresent()) {
            throw new NotFoundException();
        }
        return optional.get();
    }

    @Override
    public AppUser refreshUsername(String username) {
        if (cache != null) {
            cache.invalidate(username);
        }
        return getByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser entity = getByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException(String.format("user: %s not found", username));
        }
        return entity;
    }

    @Override
    public AppUser initializeUserIfNotExists(String username, String password, String email, boolean admin) {
        AppUser result = getByUsername(username);
        if (result == null) {
            result = initializeUser(username, password, email, admin);
        }
        return result;
    }

    @Override
    public AppUser initializeUser(String username, String password, String email, boolean admin) throws UsernameNotFoundException, EmailValidationException {
        return initializeUser(username, password, email, Arrays.asList(admin ? authProperties.getRoleAdmin() : authProperties.getRoleUser()));
    }

    @Override
    public AppUser initializeUser(String username, String password, String email, List<String> roles) throws UsernameNotFoundException, EmailValidationException {
        validationService.usernameIsValid(username);
        validationService.emailIsValid(email);

        AppUser instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(username.toLowerCase());
        instance.setEmail(email.toLowerCase());
        instance.setPassword(passwordEncoder.encode(password));
        instance.setRoles(roles);
        instance.setEnabled(true);
        if (avatarService.isEnabled()) {
            instance.setAvatar(avatarService.getAvatar(email));
        }
        AppUser entity = appUserPersistenceService.save(instance);
        refreshUsername(entity.getUsername());
        return entity;
    }

    @Override
    public AppUser updateRoles(String username, List<String> roles) {
        AppUser entity = getEntityByUsername(username);
        entity.setRoles(roles);
        entity.updateLastTokenInvalidation();
        appUserPersistenceService.save(entity);
        return refreshUsername(username);
    }

    @Override
    public AppUser registerUser(RegistrationRequest registration) {
        validationService.validateRegistration(registration.getUsername(), registration.getPassword(), registration.getEmail());

        AppUser instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(registration.getUsername().toLowerCase());
        instance.setEmail(registration.getEmail().toLowerCase());
        instance.setFirstName(registration.getFirstName());
        instance.setLastName(registration.getLastName());
        instance.setPassword(passwordEncoder.encode(registration.getPassword()));
        instance.setRoles(Arrays.asList(registrationProperties.getRole()));
        instance.setEnabled(!registrationProperties.isVerification());
        if (avatarService.isEnabled()) {
            instance.setAvatar(avatarService.getAvatar(registration.getEmail()));
        }
        handleKeyValues(instance, registration.getKeyValues());

        AppUser entity = appUserPersistenceService.save(instance);
        refreshUsername(entity.getUsername());
        return entity;
    }

    @Override
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

    @Override
    public void processRegistrationVerification(String username) {
        AppUser entity = getByUsername(username);
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
    public void delete(AppUser user) {
        appUserPersistenceService.delete(user);
        refreshUsername(user.getUsername());
    }
}
