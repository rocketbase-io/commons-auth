package io.rocketbase.commons.service.invite;

import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AppInvitePersistenceService<E extends AppInviteEntity> {

    Page<E> findAll(QueryAppInvite query, Pageable pageable);

    E save(E entity);

    AppInviteEntity invite(InviteRequest request, Instant expiration);

    Optional<E> findById(Long id);

    List<E> findAllById(Iterable<Long> ids);

    void delete(Long id);

    long deleteExpired();

    E initNewInstance();

}
