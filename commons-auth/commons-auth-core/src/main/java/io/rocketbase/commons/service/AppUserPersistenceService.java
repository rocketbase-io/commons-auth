package io.rocketbase.commons.service;

import io.rocketbase.commons.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppUserPersistenceService<S extends AppUser> {

    Optional<S> findByUsername(String username);

    Page<S> findAll(Pageable pageable);

    S save(S entity);

    S findOne(String id);

    long count();

    void delete(S entity);

    void deleteAll();

    S initializeUser(String username, String password, String email, boolean admin);

}
