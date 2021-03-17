package io.rocketbase.commons.service.capability;

import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.model.AppCapabilityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface AppCapabilityPersistenceService<E extends AppCapabilityEntity> {

    Optional<E> findById(Long id);

    List<E> findAllById(Iterable<Long> ids);

    List<E> findAllByParentId(Iterable<Long> ids);

    Page<E> findAll(QueryAppCapability query, Pageable pageable);

    default Set<E> resolveTree(Iterable<Long> ids) {
        Set<E> result = new LinkedHashSet<>();
        _resolveTree(result, findAllById(ids));
        return result;
    }

    default void _resolveTree(Set<E> result, Collection<E> starting) {
        Set<Long> parentIds = new HashSet<>();
        for (E e : starting) {
            result.add(e);
            if (e.isWithChildren() && !e.getId().equals(e.getParentId())) {
                parentIds.add(e.getId());
            }
        }
        if (!parentIds.isEmpty()) {
            _resolveTree(result, findAllByParentId(parentIds));
        }
    }

    E save(E entity);

    void delete(Long id);

    E initNewInstance();
}
