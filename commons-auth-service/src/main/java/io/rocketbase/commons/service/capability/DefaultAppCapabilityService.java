package io.rocketbase.commons.service.capability;

import com.google.common.collect.Sets;
import io.rocketbase.commons.converter.AppCapabilityConverter;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppCapabilityEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

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
        instance.setParentId(parentId);
        return applyAndSave(write, instance);
    }

    @Override
    public AppCapabilityEntity update(Long id, AppCapabilityWrite write) {
        AppCapabilityEntity entity = capabilityPersistenceService.findById(id).orElseThrow(NotFoundException::new);
        return applyAndSave(write, entity);
    }

    protected AppCapabilityEntity applyAndSave(AppCapabilityWrite write, AppCapabilityEntity instance) {
        instance.setKey(write.getKey());
        instance.setDescription(write.getDescription());
        return capabilityPersistenceService.save(instance);
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
        Set<String> result = new LinkedHashSet<>();
        resolveRecursive(result, capabilityPersistenceService.findAllById(ids));
        return result;
    }

    protected void resolveRecursive(Set<String> result, List<AppCapabilityEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        Set<Long> parentIds = new HashSet<>();
        for (AppCapabilityEntity e : entities) {
            result.add(e.getKeyPath());
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
