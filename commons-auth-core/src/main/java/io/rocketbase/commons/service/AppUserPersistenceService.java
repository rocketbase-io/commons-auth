package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppUserPersistenceService<S extends AppUserEntity> {

    Optional<S> findByUsername(String username);

    Optional<S> findByEmail(String email);

    Page<S> findAll(Pageable pageable);

    Page<S> findAll(QueryAppUser query, Pageable pageable);

    S save(S entity);

    Optional<S> findById(String id);

    long count();

    void delete(S entity);

    void deleteAll();

    S initNewInstance();
}
