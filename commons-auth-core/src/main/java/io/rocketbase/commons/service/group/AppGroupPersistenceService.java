package io.rocketbase.commons.service.group;

import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface AppGroupPersistenceService<E extends AppGroupEntity> {

    Optional<E> findById(Long id);

    List<E> findAllById(Iterable<Long> ids);

    List<E> findAllByParentId(Iterable<Long> ids);

    Page<E> findAll(QueryAppGroup query, Pageable pageable);

    E save(E entity);

    void delete(Long id);

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

    E initNewInstance();
}
