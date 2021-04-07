package io.rocketbase.commons.service.capability;

import com.google.common.collect.Sets;
import io.rocketbase.commons.converter.AppCapabilityConverter;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultAppCapabilityService implements AppCapabilityService {

    private final AppCapabilityPersistenceService<AppCapabilityEntity> capabilityPersistenceService;
    private final AppCapabilityConverter capabilityConverter;

    @Override
    public Page<AppCapabilityEntity> findAll(QueryAppCapability query, Pageable pageable) {
        return capabilityPersistenceService.findAll(query, pageable);
    }

    @Override
    public Optional<AppCapabilityEntity> findById(Long id) {
        return capabilityPersistenceService.findById(id);
    }

    @Override
    public List<AppCapabilityEntity> findByIds(Collection<Long> ids) {
        return capabilityPersistenceService.findAllById(ids);
    }

    @Override
    public AppCapabilityEntity create(AppCapabilityWrite write, Long parentId) {
        AppCapabilityEntity instance = capabilityPersistenceService.initNewInstance();
        AppCapabilityEntity parent = capabilityPersistenceService.findById(parentId).orElseThrow(NotFoundException::new);
        checkParentHasChildren(parent);
        instance.setParentId(parent.getId());
        updateValues(write, instance);
        updateKeyPath(parent.getKeyPath(), instance);
        return capabilityPersistenceService.save(instance);
    }

    @Override
    public AppCapabilityEntity update(Long id, AppCapabilityWrite write) {
        AppCapabilityEntity entity = capabilityPersistenceService.findById(id).orElseThrow(NotFoundException::new);
        AppCapabilityEntity parent = capabilityPersistenceService.findById(entity.getParentId()).orElseThrow(NotFoundException::new);

        if (!updateValues(write, entity)) {
            return entity;
        }
        boolean updatedKeyPath = updateKeyPath(parent.getKeyPath(), entity);
        if (updatedKeyPath) {
            entity = capabilityPersistenceService.save(entity);
            if (entity.isWithChildren()) {
                updateChildKeyPath(entity, capabilityPersistenceService.findAllByParentId(Arrays.asList(entity.getId())));
            }
        }
        return entity;
    }

    protected void checkParentHasChildren(AppCapabilityEntity parent) {
        if (!parent.isWithChildren()) {
            // mark parent that it has children
            parent.setWithChildren(true);
            capabilityPersistenceService.save(parent);
        }
    }

    protected boolean updateValues(AppCapabilityWrite write, AppCapabilityEntity entity) {
        boolean hasChanged = !Nulls.notNull(entity.getKey()).equals(Nulls.notNull(write.getKey())) ||
                !Nulls.notNull(entity.getDescription()).equals(Nulls.notNull(write.getDescription())) ||
                !Nulls.notNull(entity.getSystemRefId()).equals(Nulls.notNull(write.getSystemRefId()));

        entity.setKey(write.getKey());
        entity.setDescription(write.getDescription());
        entity.setSystemRefId(write.getSystemRefId());

        return hasChanged;
    }

    /**
     * calculates new keyPath
     *
     * @return true when updated otherwise false
     */
    protected boolean updateKeyPath(String parentKeyPath, AppCapabilityEntity entity) {
        String newPath = parentKeyPath + "." + entity.getKey();
        if (!newPath.equals(entity.getKeyPath())) {
            entity.setKeyPath(newPath);
            return true;
        }
        return false;
    }

    protected void updateChildKeyPath(AppCapabilityEntity parent, Collection<AppCapabilityEntity> children) {
        if (parent == null || children == null || children.isEmpty() ) {
            return;
        }
        for (AppCapabilityEntity c : children) {
            updateKeyPath(parent.getKeyPath(), c);
            capabilityPersistenceService.save(c);
            if (c.isWithChildren()) {
                updateChildKeyPath(c, capabilityPersistenceService.findAllByParentId(Arrays.asList(c.getId())));
            }
        }
    }

    @Override
    public void delete(Long id) {
        capabilityPersistenceService.delete(id);
    }

    @Override
    public Set<String> resolve(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        Set<AppCapabilityEntity> result = new LinkedHashSet<>();
        resolveRecursive(result, capabilityPersistenceService.findAllById(ids));
        return result.stream().map(AppCapabilityEntity::getKeyPath).collect(Collectors.toSet());
    }

    protected void resolveRecursive(Set<AppCapabilityEntity> result, List<AppCapabilityEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        Set<Long> parentIds = new HashSet<>();
        for (AppCapabilityEntity e : entities) {
            result.add(e);
            if (e.isWithChildren()) {
                parentIds.add(e.getId());
            }
        }
        resolveRecursive(result, capabilityPersistenceService.findAllByParentId(parentIds));
    }

    @Override
    public Set<AppCapabilityRead> lookupIds(Collection<Long> ids) {
        return Sets.newHashSet(capabilityConverter.fromEntities(capabilityPersistenceService.findAllById(ids)));
    }
}
