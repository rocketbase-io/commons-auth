package io.rocketbase.commons.service.group;

import com.google.common.collect.Sets;
import io.rocketbase.commons.converter.AppGroupConverter;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppGroupEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        instance.setParentId(parentId);
        return applyAndSave(write, instance);
    }

    @Override
    public AppGroupEntity update(Long id, AppGroupWrite write) {
        AppGroupEntity entity = groupPersistenceService.findById(id).orElseThrow(NotFoundException::new);
        return applyAndSave(write, entity);
    }

    protected AppGroupEntity applyAndSave(AppGroupWrite write, AppGroupEntity instance) {
        instance.setName(write.getName());
        instance.setSystemRefId(write.getSystemRefId());
        instance.setDescription(write.getDescription());
        instance.setCapabilityIds(write.getCapabilityIds());
        instance.setKeyValues(write.getKeyValues());
        return groupPersistenceService.save(instance);
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
    public Set<AppGroupRead> lookupIds(Collection<Long> ids) {
        return Sets.newHashSet(groupConverter.fromEntities(groupPersistenceService.findAllById(ids)));
    }
}