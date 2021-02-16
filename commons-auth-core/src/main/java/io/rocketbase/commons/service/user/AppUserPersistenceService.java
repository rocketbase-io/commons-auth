package io.rocketbase.commons.service.user;

import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppUserPersistenceService<E extends AppUserEntity> {

    Optional<E> findById(String id);

    Optional<E> findByUsername(String username);

    Optional<E> findByEmail(String email);

    Page<E> findAll(QueryAppUser query, Pageable pageable);

    E save(E entity);

    void delete(String id);

    E initNewInstance();
}
