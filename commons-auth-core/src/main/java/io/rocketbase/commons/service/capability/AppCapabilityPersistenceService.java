package io.rocketbase.commons.service.capability;

import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.model.AppCapabilityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AppCapabilityPersistenceService<E extends AppCapabilityEntity> {

    Optional<E> findById(Long id);

    List<E> findAllById(Iterable<Long> ids);

    List<E> findAllByParentId(Iterable<Long> ids);

    Page<E> findAll(QueryAppCapability query, Pageable pageable);

    E save(E entity);

    void delete(Long id);

    E initNewInstance();
}
