package io.rocketbase.commons.test;

import io.rocketbase.commons.config.AuthConfiguration;
import io.rocketbase.commons.model.AppUserTestEntity;
import io.rocketbase.commons.service.AppUserPersistenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppUserPersistenceTestService implements AppUserPersistenceService<AppUserTestEntity> {

    private Map<String, AppUserTestEntity> userMap = new HashMap<>();

    @Resource
    private AuthConfiguration authConfiguration;

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

        userMap.put("user", buildAppUser("user", authConfiguration.getRoleNameUser(), "user@rocketbase.io"));
        userMap.put("admin", buildAppUser("admin", authConfiguration.getRoleNameAdmin(), "user@rocketbase.io"));
        AppUserTestEntity disabled = buildAppUser("disabled", authConfiguration.getRoleNameAdmin(), "disabled@rocketbase.io");
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
    public AppUserTestEntity save(AppUserTestEntity entity) {
        userMap.put(entity.getUsername(), entity);
        return userMap.get(entity.getUsername());
    }

    @Override
    public AppUserTestEntity findOne(String id) {
        for (AppUserTestEntity user : userMap.values()) {
            if (id.equals(user.getId())) {
                return user.clone();
            }
        }
        return null;
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
                .created(LocalDateTime.now())
                .roles(new ArrayList<>())
                .build();
    }
}
