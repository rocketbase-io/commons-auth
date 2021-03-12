package io.rocketbase.commons.service.client;

import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.model.AppClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AppClientPersistenceService<E extends AppClientEntity> {

    Optional<E> findById(Long id);

    List<E> findAllById(Iterable<Long> ids);

    Page<E> findAll(QueryAppClient query, Pageable pageable);

    E save(E entity);

    void delete(Long id);

    E initNewInstance();
}
