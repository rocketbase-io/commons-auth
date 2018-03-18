package io.rocketbase.commons.service;

import io.rocketbase.commons.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppUserPersistenceService<S extends AppUser> {

    Optional<S> findByUsername(String username);

    Optional<S> findByEmail(String email);

    Page<S> findAll(Pageable pageable);

    S save(S entity);

    S findOne(String id);

    long count();

    void delete(S entity);

    void deleteAll();

    S initNewInstance();
}
