package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppCapabilityConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class AppCapabilityApiService implements AppCapabilityApi, BaseApiService {

    private final AppCapabilityService service;
    private final AppCapabilityConverter converter;

    @Override
    public PageableResult<AppCapabilityRead> find(QueryAppCapability query, Pageable pageable) {
        Page<AppCapabilityEntity> page = service.findAll(query, pageable);
        return PageableResult.contentPage(converter.fromEntities(page.getContent()), page);
    }

    @Override
    public Optional<AppCapabilityRead> findById(Long id) {
        Optional<AppCapabilityEntity> optional = service.findById(id);
        return optional.isPresent() ? Optional.of(converter.fromEntity(optional.get())) : Optional.empty();
    }

    @Override
    public AppCapabilityRead create(Long parentId, AppCapabilityWrite write) {
        AppCapabilityEntity entity = service.create(write, parentId);
        return converter.fromEntity(entity);
    }

    @Override
    public AppCapabilityRead update(Long id, AppCapabilityWrite write) {
        AppCapabilityEntity entity = service.update(id, write);
        return converter.fromEntity(entity);
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }
}
