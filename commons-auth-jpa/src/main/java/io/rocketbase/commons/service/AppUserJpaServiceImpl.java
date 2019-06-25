package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.repository.AppUserJpaRepository;
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
public class AppUserJpaServiceImpl implements AppUserPersistenceService<AppUserJpaEntity> {

    private final AppUserJpaRepository repository;

    @Override
    public Optional<AppUserJpaEntity> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Optional<AppUserJpaEntity> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Page<AppUserJpaEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<AppUserJpaEntity> findAll(QueryAppUser query, Pageable pageable) {
        AppUserJpaEntity example = new AppUserJpaEntity();
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
    public AppUserJpaEntity save(AppUserJpaEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<AppUserJpaEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void delete(AppUserJpaEntity entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public AppUserJpaEntity initNewInstance() {
        return AppUserJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(LocalDateTime.now())
                .roles(new ArrayList<>())
                .build();
    }
}
