package io.rocketbase.commons.service.capability;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityShort;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.model.AppCapabilityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface AppCapabilityService {

    Page<AppCapabilityEntity> findAll(QueryAppCapability query, Pageable pageable);

    AppCapabilityEntity save(AppCapabilityEntity entity);

    Optional<AppCapabilityEntity> findById(Long id);

    List<AppCapabilityEntity> findByIds(Collection<Long> ids);

    AppCapabilityEntity create(AppCapabilityWrite write, Long parentId);

    AppCapabilityRead update(Long id, AppCapabilityWrite write);

    /**
     * will delete also their children-tree
     */
    void delete(Long id);

    /**
     * lookup ids and fetches all children until final leave
     *
     * @param ids collection of entity ids that represent the starting point
     * @return unique set of all groups and their children
     */
    Set<String> resolve(Collection<Long> ids);

    /**
     * lookup ids and fetches their keyPath
     * @param ids collection of entity ids
     * @return unique set of keyPath
     */
    Set<AppCapabilityRead> lookupIds(Collection<Long> ids);

    default Set<AppCapabilityShort> lookupIdsShort(Collection<Long> ids) {
        if (ids == null) {
            return null;
        }
        return lookupIds(ids).stream().map(AppCapabilityRead::toShort).collect(Collectors.toSet());
    }

}
