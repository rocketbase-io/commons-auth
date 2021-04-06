package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppGroupConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppGroupEntity;
import io.rocketbase.commons.service.group.AppGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class AppGroupApiService implements AppGroupApi, BaseApiService {

    private final AppGroupService service;
    private final AppGroupConverter converter;

    @Override
    public PageableResult<AppGroupRead> find(QueryAppGroup query, Pageable pageable) {
        Page<AppGroupEntity> page = service.findAll(query, pageable);
        return PageableResult.contentPage(converter.fromEntities(page.getContent()), page);
    }

    @Override
    public Optional<AppGroupRead> findById(Long id) {
        Optional<AppGroupEntity> optional = service.findById(id);
        return optional.isPresent() ? Optional.of(converter.fromEntity(optional.get())) : Optional.empty();
    }

    @Override
    public AppGroupRead create(Long parentId, AppGroupWrite write) {
        AppGroupEntity entity = service.create(write, parentId);
        return converter.fromEntity(entity);
    }

    @Override
    public AppGroupRead update(Long id, AppGroupWrite write) {
        AppGroupEntity entity = service.update(id, write);
        return converter.fromEntity(entity);
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }
}
