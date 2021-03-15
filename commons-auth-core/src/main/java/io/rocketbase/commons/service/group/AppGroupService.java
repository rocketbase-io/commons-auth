package io.rocketbase.commons.service.group;

import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface AppGroupService {

    Page<AppGroupEntity> findAll(QueryAppGroup query, Pageable pageable);

    AppGroupEntity create(AppGroupWrite write, Long parentId);

    AppGroupEntity update(Long id, AppGroupWrite write);

    Optional<AppGroupEntity> findById(Long id);

    List<AppGroupEntity> findByIds(Collection<Long> ids);

    /**
     * will delete also their children-tree
     */
    void delete(Long id);

    Set<AppGroupRead> lookupIds(Collection<Long> ids);

    default Set<AppGroupShort> lookupIdsShort(Collection<Long> ids) {
        if (ids == null) {
            return null;
        }
        return lookupIds(ids).stream().map(AppGroupRead::toShort).collect(Collectors.toSet());
    }
}
