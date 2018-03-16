package io.rocketbase.commons.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AppUserService implements UserDetailsService {

    @Resource
    private AppUserPersistenceService appUserPersistenceService;

    @Resource
    private PasswordEncoder passwordEncoder;

    private LoadingCache<String, Optional<AppUser>> cache;

    @PostConstruct
    public void postConstruct() {
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Optional<AppUser>>() {
                    @Override
                    public Optional<AppUser> load(String key) {
                        return appUserPersistenceService.findByUsername(key);
                    }
                });
    }

    @SneakyThrows
    public AppUser getByUsername(String username) {
        Optional<AppUser> userEntity = cache.get(username);
        if (userEntity.isPresent()) {
            return userEntity.get();
        }
        return null;
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
        cache.refresh(username);
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
        return appUserPersistenceService.initializeUser(username, password, email, admin);
    }
}
