package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.repository.AppUserRepository;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
    public Page<AppUserEntity> findAll(QueryAppUser query, Pageable pageable) {
        AppUserEntity example = new AppUserEntity();
        example.setKeyValueMap(null);
        example.setEnabled(true);
        if (query != null) {
            example.setUsername(query.getUsername());
            example.setFirstName(query.getFirstName());
            example.setLastName(query.getLastName());
            example.setEmail(query.getEmail());
            example.setEnabled(Nulls.notNull(query.getEnabled(), true));
        }
        ExampleMatcher matcherConfig = ExampleMatcher.matchingAny()
                .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnoreNullValues();
        return repository.findAll(Example.of(example, matcherConfig), pageable);
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
