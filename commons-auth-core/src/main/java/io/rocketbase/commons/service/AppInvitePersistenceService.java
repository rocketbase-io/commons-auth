package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppInvitePersistenceService<S extends AppInviteEntity> {

    Page<S> findAll(QueryAppInvite query, Pageable pageable);

    S save(S entity);

    Optional<S> findById(String id);

    long count();

    void delete(S entity);

    void deleteAll();

    S initNewInstance();

    long deleteExpired();
}
