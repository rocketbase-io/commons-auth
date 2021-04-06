package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppClientConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.dto.appclient.AppClientWrite;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.model.AppClientEntity;
import io.rocketbase.commons.service.client.AppClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class AppClientApiService implements AppClientApi, BaseApiService {

    private final AppClientService service;
    private final AppClientConverter converter;

    @Override
    public PageableResult<AppClientRead> find(QueryAppClient query, Pageable pageable) {
        Page<AppClientEntity> page = service.findAll(query, pageable);
        return PageableResult.contentPage(converter.fromEntities(page.getContent()), page);
    }

    @Override
    public Optional<AppClientRead> findById(Long id) {
        Optional<AppClientEntity> optional = service.findById(id);
        return optional.isPresent() ? Optional.of(converter.fromEntity(optional.get())) : Optional.empty();
    }

    @Override
    public AppClientRead create(AppClientWrite write) {
        AppClientEntity entity = service.create(write);
        return converter.fromEntity(entity);
    }

    @Override
    public AppClientRead update(Long id, AppClientWrite write) {
        AppClientEntity entity = service.update(id, write);
        return converter.fromEntity(entity);
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }
}
