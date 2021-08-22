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
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.event.EmailChangeEvent;
import io.rocketbase.commons.event.PasswordEvent;
import io.rocketbase.commons.event.UsernameChangeEvent;
import io.rocketbase.commons.exception.*;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.model.user.SimpleUserProfile;
import io.rocketbase.commons.model.user.SimpleUserSetting;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.service.avatar.AvatarService;
import io.rocketbase.commons.service.validation.ValidationErrorCodeService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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


    /**
     * used to check if key values have changed within cache
     */
    protected int getCacheHash(AppUserEntity entity) {
        return String.format("%s.%s.%s", entity.getId(), entity.getUsername(), entity.getEmail()).hashCode();
    }

    protected AppUserEntity getEntityByUsernameOrId(String usernameOrId) {
        Optional<AppUserEntity> optional = appUserPersistenceService.findByUsername(usernameOrId);
        if (!optional.isPresent()) {
            return appUserPersistenceService.findById(usernameOrId).orElseThrow(NotFoundException::new);
        }
        return optional.get();
    }

    protected AppUserEntity saveAndInvalidate(AppUserEntity entity) {
        // keep original key hash - in some cases the key may change after save (for example email/user change...)
        int hash = getCacheHash(entity);
        invalidateCache(entity);

        AppUserEntity saved = appUserPersistenceService.save(entity);
        // in special cases also the new stored keys needs to get invalidated. for example after email-change an previous email-lookup should return Optional.of(AppUserEntity) now instead of Optional.empty()
        if (getCacheHash(saved) != hash) {
            invalidateCache(saved);
        }

        return saved;
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
        return saveAndInvalidate(entity);
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
        entity = saveAndInvalidate(entity);

        applicationEventPublisher.publishEvent(new PasswordEvent(this, entity, CHANGED));
        return entity;
    }

    @Override
    public AppUserEntity save(String usernameOrId, AppUserUpdate update) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        entity.setProfile(checkAvatar(update.getProfile(), entity.getEmail()));
        entity.setSetting(update.getSetting());
        entity.setCapabilityIds(update.getCapabilityIds());
        entity.setEnabled(update.getEnabled());
        entity.setLocked(update.getLocked());
        entity.setActiveTeamId(update.getActiveTeamId());
        entity.setKeyValues(update.getKeyValues());
        return saveAndInvalidate(entity);
    }

    @Override
    public AppUserEntity patch(String usernameOrId, AppUserUpdate update) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        if (update.getProfile() != null) {
            entity.setProfile(checkAvatar(update.getProfile(), entity.getEmail()));
        }
        if (update.getSetting() != null) {
            entity.setSetting(update.getSetting());
        }
        if (update.getCapabilityIds() != null && !update.getCapabilityIds().isEmpty()) {
            entity.setCapabilityIds(update.getCapabilityIds());
        }
        if (update.getEnabled() != null) {
            entity.setEnabled(update.getEnabled());
        }
        if (update.getLocked() != null) {
            entity.setLocked(update.getLocked());
        }
        if (update.getActiveTeamId() != null) {
            entity.setActiveTeamId(update.getActiveTeamId());
        }
        handleKeyValues(entity, update.getKeyValues());
        return saveAndInvalidate(entity);
    }

    @Override
    public AppUserEntity updateKeyValues(String usernameOrId, Map<String, String> keyValues) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        handleKeyValues(entity, keyValues);
        return saveAndInvalidate(entity);
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


    @Override
    public AppUserEntity initializeUser(AppUserCreate userCreate) throws UsernameNotFoundException, EmailValidationException {
        validationService.usernameIsValid("username", userCreate.getUsername());
        validationService.emailIsValid("email", userCreate.getEmail());

        AppUserEntity instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(userCreate.getUsername().toLowerCase());
        instance.setEmail(userCreate.getEmail().toLowerCase());
        instance.setPassword(passwordEncoder.encode(userCreate.getPassword()));
        instance.setProfile(checkAvatar(SimpleUserProfile.builder()
                .firstName(userCreate.getFirstName())
                .lastName(userCreate.getLastName())
                .avatar(userCreate.getAvatar())
                .build(), userCreate.getEmail()));
        instance.setSetting(SimpleUserSetting.init(LocaleContextHolder.getLocale()));
        instance.setKeyValues(userCreate.getKeyValues());
        instance.setCapabilityIds(userCreate.getCapabilityIds());
        instance.setGroupIds(userCreate.getGroupIds());
        instance.setEnabled(userCreate.isEnabled());
        instance.setLocked(false);

        instance.validateKeyValues();

        return saveAndInvalidate(instance);
    }

    @Override
    public AppUserEntity registerUser(RegistrationRequest registration) throws RegistrationException {
        validationService.registrationIsValid(registration.getUsername(), registration.getPassword(), registration.getEmail());

        AppUserEntity instance = appUserPersistenceService.initNewInstance();
        instance.setUsername(registration.getUsername().toLowerCase());
        instance.setEmail(registration.getEmail().toLowerCase());
        instance.setPassword(passwordEncoder.encode(registration.getPassword()));
        instance.setProfile(checkAvatar(SimpleUserProfile.builder()
                .firstName(registration.getFirstName())
                .lastName(registration.getLastName())
                .build(), registration.getEmail()));
        instance.setSetting(SimpleUserSetting.init(LocaleContextHolder.getLocale()));
        instance.setKeyValues(registration.getKeyValues());
        instance.setCapabilityIds(registrationProperties.getCapabilityIds());
        instance.setGroupIds(registrationProperties.getGroupIds());
        instance.setEnabled(!registrationProperties.isVerification());
        instance.setLocked(false);

        instance.validateKeyValues();

        return saveAndInvalidate(instance);
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

        saveAndInvalidate(entity);
    }

    @Override
    public AppUserEntity changeUsername(String userId, String newUsername) throws UsernameValidationException {
        AppUserEntity entity = appUserPersistenceService.findById(userId).orElseThrow(NotFoundException::new);
        // invalidate will old values
        invalidateCache(entity);

        validationService.usernameIsValid("newUsername", newUsername);
        String oldUsername = entity.getUsername();
        entity.setUsername(newUsername);

        entity = saveAndInvalidate(entity);
        applicationEventPublisher.publishEvent(new UsernameChangeEvent(this, oldUsername, entity));
        return entity;
    }

    @Override
    public AppUserEntity changeEmail(String userId, String newEmail) throws EmailValidationException {
        AppUserEntity entity = appUserPersistenceService.findById(userId).orElseThrow(NotFoundException::new);
        // invalidate will old values
        invalidateCache(entity);

        validationService.emailIsValid("newEmail", newEmail);
        String oldEmail = entity.getEmail();
        entity.setEmail(newEmail);

        entity = saveAndInvalidate(entity);
        applicationEventPublisher.publishEvent(new EmailChangeEvent(this, oldEmail, entity));
        return entity;
    }

    @Override
    public void delete(String usernameOrId) {
        AppUserEntity entity = getEntityByUsernameOrId(usernameOrId);
        appUserPersistenceService.delete(entity.getId());
        invalidateCache(entity);
    }

    @Override
    public Page<AppUserEntity> findAll(QueryAppUser query, Pageable pageable) {
        return appUserPersistenceService.findAll(query, pageable);
    }

    protected UserProfile checkAvatar(UserProfile userProfile, String email) {
        if (userProfile == null) {
            return null;
        }
        if (!StringUtils.hasText(userProfile.getAvatar()) && avatarService.isEnabled() && email != null) {
            userProfile.setAvatar(avatarService.getAvatar(email));
        }
        return userProfile;
    }

    @Builder
    @Data
    private static class CacheFilter {
        private String username;
        private String email;
        private String id;
    }
}
