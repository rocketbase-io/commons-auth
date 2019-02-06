package io.rocketbase.commons.service;

import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AppUserJpaServiceImpl implements AppUserPersistenceService<AppUserEntity> {

    private final AppUserRepository repository;

    @Override
    public Optional<AppUserEntity> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Optional<AppUserEntity> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Page<AppUserEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public AppUserEntity save(AppUserEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<AppUserEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void delete(AppUserEntity entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public AppUserEntity initNewInstance() {
        return AppUserEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(LocalDateTime.now())
                .roles(new ArrayList<>())
                .build();
    }
}
