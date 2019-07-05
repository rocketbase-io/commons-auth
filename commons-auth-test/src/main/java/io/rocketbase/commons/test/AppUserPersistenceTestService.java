package io.rocketbase.commons.test;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.service.AppUserPersistenceService;
import io.rocketbase.commons.test.model.AppUserTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;

public class AppUserPersistenceTestService implements AppUserPersistenceService<AppUserTestEntity> {

    private Map<String, AppUserTestEntity> userMap = new HashMap<>();

    @Resource
    private PasswordEncoder passwordEncoder;

    private AppUserTestEntity buildAppUser(String username, String role, String email) {
        return AppUserTestEntity.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode("pw"))
                .email(email)
                .enabled(true)
                .roles(Arrays.asList(role))
                .build();
    }

    @PostConstruct
    public void init() {
        resetData();
    }

    public void resetData() {
        userMap.clear();

        AuthProperties authProperties = new AuthProperties();

        userMap.put("user", buildAppUser("user", authProperties.getRoleUser(), "user@rocketbase.io"));
        userMap.put("admin", buildAppUser("admin", authProperties.getRoleAdmin(), "user@rocketbase.io"));
        AppUserTestEntity disabled = buildAppUser("disabled", authProperties.getRoleAdmin(), "disabled@rocketbase.io");
        disabled.setEnabled(false);
        userMap.put("disabled", disabled);
    }


    @Override
    public Optional<AppUserTestEntity> findByUsername(String username) {
        return userMap.containsKey(username) ? Optional.of(userMap.get(username).clone()) : Optional.empty();
    }

    @Override
    public Optional<AppUserTestEntity> findByEmail(String email) {
        for (AppUserTestEntity user : userMap.values()) {
            if (email.equals(user.getEmail())) {
                return Optional.of(user.clone());
            }
        }
        return Optional.empty();
    }

    @Override
    public Page findAll(Pageable pageable) {
        return new PageImpl(new ArrayList(userMap.values()), pageable, userMap.size());
    }

    @Override
    public Page<AppUserTestEntity> findAll(QueryAppUser query, Pageable pageable) {
        return new PageImpl(new ArrayList(userMap.values()), pageable, userMap.size());
    }

    @Override
    public AppUserTestEntity save(AppUserTestEntity entity) {
        userMap.put(entity.getUsername(), entity);
        return userMap.get(entity.getUsername());
    }

    @Override
    public Optional<AppUserTestEntity> findById(String id) {
        for (AppUserTestEntity user : userMap.values()) {
            if (id.equals(user.getId())) {
                return Optional.of(user.clone());
            }
        }
        return Optional.empty();
    }

    @Override
    public long count() {
        return userMap.size();
    }

    @Override
    public void delete(AppUserTestEntity entity) {
        userMap.remove(entity.getUsername());
    }

    @Override
    public void deleteAll() {
    }

    @Override
    public AppUserTestEntity initNewInstance() {
        return AppUserTestEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .roles(new ArrayList<>())
                .build();
    }
}
