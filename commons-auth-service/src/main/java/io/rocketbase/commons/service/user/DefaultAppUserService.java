package io.rocketbase.commons.service.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.event.PasswordEvent;
import io.rocketbase.commons.event.UpdateProfileEvent;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.exception.PasswordValidationException;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.service.AppUserPersistenceService;
import io.rocketbase.commons.service.avatar.AvatarService;
import io.rocketbase.commons.service.validation.ValidationErrorCodeService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.rocketbase.commons.event.PasswordEvent.PasswordProcessType.CHANGED;

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

    @Resource
    private ValidationErrorCodeService validationErrorCodeService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

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
    public AppUserEntity updateLastLogin(String usernameOrId) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        entity.updateLastLogin();

        invalidateCache(entity);
        return appUserPersistenceService.save(entity);
    }

    @Override
    public AppUserEntity performUpdatePassword(String usernameOrId, PasswordChangeRequest passwordChangeRequest) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        // check old password
        if (!passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), entity.getPassword())) {
            throw new PasswordValidationException(Sets.newHashSet(validationErrorCodeService.passwordError("currentPassword", PasswordErrorCodes.INVALID_CURRENT_PASSWORD)));
        }

        validationService.passwordIsValid("newPassword", passwordChangeRequest.getNewPassword());
        return updatePasswordUnchecked(entity.getUsername(), passwordChangeRequest.getNewPassword());
    }

    @Override
    public AppUserEntity updatePasswordUnchecked(String usernameOrId, String newPassword) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        entity.setPassword(passwordEncoder.encode(newPassword));
        entity.updateLastTokenInvalidation();

        invalidateCache(entity);
        entity = appUserPersistenceService.save(entity);

        applicationEventPublisher.publishEvent(new PasswordEvent(this, entity, CHANGED));

        return entity;
    }

    @Override
    public AppUserEntity patch(String usernameOrId, AppUserUpdate update) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        if (shouldPatch(update.getFirstName())) {
            entity.setFirstName(update.getFirstName());
        }
        if (shouldPatch(update.getLastName())) {
            entity.setLastName(update.getLastName());
        }
        if (update.getRoles() != null && !update.getRoles().isEmpty()) {
            entity.setRoles(update.getRoles());
        }
        if (update.getEnabled() != null) {
            entity.setEnabled(update.getEnabled());
        }
        handleKeyValues(entity, update.getKeyValues());

        entity = appUserPersistenceService.save(entity);

        if (shouldPatch(update.getPassword())) {
            validationService.passwordIsValid("password", update.getPassword());
            updatePasswordUnchecked(entity.getUsername(), update.getPassword());
        }
        invalidateCache(entity);
        return entity;
    }

    @Override
    public AppUserEntity updateProfile(String usernameOrId, UpdateProfileRequest updateProfile) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        entity.setFirstName(updateProfile.getFirstName());
        entity.setLastName(updateProfile.getLastName());
        entity.setAvatar(updateProfile.getAvatar());
        handleKeyValues(entity, updateProfile.getKeyValues());

        invalidateCache(entity);
        entity = appUserPersistenceService.save(entity);

        applicationEventPublisher.publishEvent(new UpdateProfileEvent(this, entity));

        return entity;
    }

    @Override
    public AppUserEntity updateKeyValues(String usernameOrId, Map<String, String> keyValues) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        handleKeyValues(entity, keyValues);

        invalidateCache(entity);
        return appUserPersistenceService.save(entity);
    }

    private AppUserEntity getEntityByUsernameOrId(String usernameOrId) {
        Optional<AppUserEntity> optional = appUserPersistenceService.findByUsername(usernameOrId);
        if (!optional.isPresent()) {
            return appUserPersistenceService.findById(usernameOrId).orElseThrow(NotFoundException::new);
        }
        return optional.get();
    }

    @Override
    public void invalidateCache(AppUserReference appUser) {
        if (cache != null && appUser != null) {
            if (appUser.getId() != null) {
                cache.invalidate(CacheFilter.builder().id(appUser.getId()).build());
            }
            if (appUser.getUsername() != null) {
                cache.invalidate(CacheFilter.builder().username(appUser.getUsername()).build());
            }
            if (appUser.getEmail() != null) {
                cache.invalidate(CacheFilter.builder().email(appUser.getEmail()).build());
            }
        }
    }

    /**
     * lookup user also via email-adress if used...
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserEntity entity = getByUsername(username);
        if (entity == null) {
            entity = findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format("user: %s not found", username)));
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
        return initializeUser(AppUserCreate.builder()
                .username(username)
                .password(password)
                .email(email)
                .admin(admin)
                .enabled(true)
                .build());
    }

    @Override
    public AppUserEntity initializeUser(AppUserCreate userCreate) throws UsernameNotFoundException, EmailValidationException {
        validationService.usernameIsValid("username", userCreate.getUsername());
        validationService.emailIsValid("email", userCreate.getEmail());

        AppUserEntity instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(userCreate.getUsername().toLowerCase());
        instance.setEmail(userCreate.getEmail().toLowerCase());
        instance.setPassword(passwordEncoder.encode(userCreate.getPassword()));
        instance.setFirstName(userCreate.getFirstName());
        instance.setLastName(userCreate.getLastName());
        handleKeyValues(instance, userCreate.getKeyValues());

        List<String> roles = new ArrayList<>();
        if (userCreate.getAdmin() != null) {
            roles.add(userCreate.getAdmin() ? authProperties.getRoleAdmin() : authProperties.getRoleUser());
        }
        if (userCreate.getRoles() != null) {
            roles.addAll(userCreate.getRoles());
        }
        instance.setRoles(convertRoles(roles));
        instance.setEnabled(userCreate.isEnabled());
        if (StringUtils.isEmpty(userCreate.getAvatar()) && avatarService.isEnabled()) {
            instance.setAvatar(avatarService.getAvatar(userCreate.getEmail()));
        }
        AppUserEntity entity = appUserPersistenceService.save(instance);
        // invalid cache in case of email + username lookup for example
        invalidateCache(entity);
        return entity;
    }

    @Override
    public AppUserEntity updateEnabled(String usernameOrId, boolean enabled) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        entity.setEnabled(enabled);
        entity = appUserPersistenceService.save(entity);
        invalidateCache(entity);
        return entity;
    }

    protected List<String> convertRoles(List<String> roles) {
        if (roles == null) {
            return null;
        }
        return new ArrayList<>(roles.stream().map(r -> r.replaceAll("^ROLE_", ""))
                .collect(Collectors.toSet()));
    }

    @Override
    public AppUserEntity updateRoles(String usernameOrId, List<String> roles) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        entity.setRoles(convertRoles(roles));
        entity.updateLastTokenInvalidation();

        invalidateCache(entity);
        return appUserPersistenceService.save(entity);
    }

    @Override
    public AppUserEntity registerUser(RegistrationRequest registration) throws RegistrationException {
        validationService.registrationIsValid(registration.getUsername(), registration.getPassword(), registration.getEmail());

        AppUserEntity instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(registration.getUsername().toLowerCase());
        instance.setEmail(registration.getEmail().toLowerCase());
        instance.setFirstName(registration.getFirstName());
        instance.setLastName(registration.getLastName());
        instance.setPassword(passwordEncoder.encode(registration.getPassword()));
        instance.setRoles(convertRoles(Arrays.asList(registrationProperties.getRole())));
        instance.setEnabled(!registrationProperties.isVerification());
        if (avatarService.isEnabled()) {
            instance.setAvatar(avatarService.getAvatar(registration.getEmail()));
        }
        handleKeyValues(instance, registration.getKeyValues());

        AppUserEntity entity = appUserPersistenceService.save(instance);
        // invalid cache in case of email + username lookup for example
        invalidateCache(entity);
        return entity;
    }


    protected void handleKeyValues(AppUserEntity user, Map<String, String> keyValues) {
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

        invalidateCache(entity);

        appUserPersistenceService.save(entity);
    }

    @Override
    public void delete(AppUserEntity user) {
        appUserPersistenceService.delete(user);
        invalidateCache(user);
    }

    @Override
    public Page<AppUserEntity> findAll(QueryAppUser query, Pageable pageable) {
        return appUserPersistenceService.findAll(query, pageable);
    }

    @Builder
    @Data
    private static class CacheFilter {
        private String username;
        private String email;
        private String id;
    }
}
