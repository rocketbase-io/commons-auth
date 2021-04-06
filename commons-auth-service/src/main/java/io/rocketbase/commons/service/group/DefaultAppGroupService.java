package io.rocketbase.commons.service.group;

import com.google.common.collect.Sets;
import io.rocketbase.commons.converter.AppGroupConverter;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppGroupEntity;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

@RequiredArgsConstructor
public class DefaultAppGroupService implements AppGroupService {

    private final AppGroupPersistenceService<AppGroupEntity> groupPersistenceService;
    private final AppGroupConverter<AppGroupEntity> groupConverter;

    @Override
    public Page<AppGroupEntity> findAll(QueryAppGroup query, Pageable pageable) {
        return groupPersistenceService.findAll(query, pageable);
    }

    @Override
    public AppGroupEntity create(AppGroupWrite write, Long parentId) {
        AppGroupEntity instance = groupPersistenceService.initNewInstance();
        AppGroupEntity parent = groupPersistenceService.findById(parentId).orElseThrow(NotFoundException::new);
        checkParentHasChildren(parent);
        instance.setParentId(parent.getId());
        updateValues(write, instance);
        updateNamePath(parent.getNamePath(), instance);
        return groupPersistenceService.save(instance);
    }

    @Override
    public AppGroupEntity update(Long id, AppGroupWrite write) {
        AppGroupEntity entity = groupPersistenceService.findById(id).orElseThrow(NotFoundException::new);
        AppGroupEntity parent = groupPersistenceService.findById(entity.getParentId()).orElseThrow(NotFoundException::new);

        if (!updateValues(write, entity)) {
            return entity;
        }
        boolean updatedKeyPath = updateNamePath(parent.getNamePath(), entity);
        if (updatedKeyPath) {
            entity = groupPersistenceService.save(entity);
            if (entity.isWithChildren()) {
                updateChildKeyPath(entity, groupPersistenceService.findAllByParentId(Arrays.asList(entity.getId())));
            }
        }
        return entity;
    }

    protected void checkParentHasChildren(AppGroupEntity parent) {
        if (!parent.isWithChildren()) {
            // mark parent that it has children
            parent.setWithChildren(true);
            groupPersistenceService.save(parent);
        }
    }

    protected boolean updateValues(AppGroupWrite write, AppGroupEntity entity) {
        boolean hasChanged = !Nulls.notNull(entity.getName()).equals(Nulls.notNull(write.getName())) ||
                !Nulls.notNull(entity.getSystemRefId()).equals(Nulls.notNull(write.getSystemRefId())) ||
                !Nulls.notNull(entity.getDescription()).equals(Nulls.notNull(write.getDescription())) ||
                !Nulls.notNull(entity.getCapabilityIds()).equals(Nulls.notNull(write.getCapabilityIds())) ||
                !Nulls.notNull(entity.getKeyValues()).equals(Nulls.notNull(write.getKeyValues()));

        entity.setName(write.getName());
        entity.setSystemRefId(write.getSystemRefId());
        entity.setDescription(write.getDescription());
        entity.setCapabilityIds(write.getCapabilityIds());
        entity.setKeyValues(write.getKeyValues());

        return hasChanged;
    }

    /**
     * calculates new keyPath
     *
     * @return true when updated otherwise false
     */
    protected boolean updateNamePath(String parentNamePath, AppGroupEntity entity) {
        String newPath = parentNamePath + "/" + entity.getName();
        if (!newPath.equals(entity.getNamePath())) {
            entity.setNamePath(newPath);
            return true;
        }
        return false;
    }

    protected void updateChildKeyPath(AppGroupEntity parent, Collection<AppGroupEntity> children) {
        if (parent == null || children == null || children.isEmpty() ) {
            return;
        }
        for (AppGroupEntity c : children) {
            updateNamePath(parent.getNamePath(), c);
            groupPersistenceService.save(c);
            if (c.isWithChildren()) {
                updateChildKeyPath(c, groupPersistenceService.findAllByParentId(Arrays.asList(c.getId())));
            }
        }
    }

    @Override
    public Optional<AppGroupEntity> findById(Long id) {
        return groupPersistenceService.findById(id);
    }

    @Override
    public List<AppGroupEntity> findByIds(Collection<Long> ids) {
        return groupPersistenceService.findAllById(ids);
    }

    @Override
    public void delete(Long id) {
        groupPersistenceService.delete(id);
    }

    @Override
    public Set<AppGroupEntity> followTreeUpwards(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        Set<AppGroupEntity> result = new HashSet<>();
        followUpwards(result, findByIds(ids));
        return result;
    }

    protected void followUpwards(Set<AppGroupEntity> result, Collection<AppGroupEntity> entities) {
        if (entities == null) {
            return;
        }

        result.addAll(entities);
        Set<Long> parentIds = new HashSet<>();
        for (AppGroupEntity e : entities) {
            if (!e.getParentId().equals(e.getId())) {
                parentIds.add(e.getParentId());
            }
        }
        if (!parentIds.isEmpty()) {
            followUpwards(result, findByIds(parentIds));
        }
    }

    @Override
    public Set<AppGroupRead> lookupIds(Collection<Long> ids) {
        return Sets.newHashSet(groupConverter.fromEntities(groupPersistenceService.findAllById(ids)));
    }
}
