package io.rocketbase.commons.service.group;

import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppGroupPersistenceService<E extends AppGroupEntity> {

    Optional<E> findById(String id);

    Page<E> findAll(QueryAppGroup query, Pageable pageable);

    E save(E entity);

    void delete(String id);

    E initNewInstance();
}
