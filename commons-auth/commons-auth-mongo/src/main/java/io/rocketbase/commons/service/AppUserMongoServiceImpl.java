package io.rocketbase.commons.service;

import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.repository.AppUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserMongoServiceImpl implements AppUserPersistenceService<AppUserEntity> {

    @Resource
    private AppUserRepository repository;

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
    public AppUserEntity findOne(String id) {
        return repository.findOne(id);
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
