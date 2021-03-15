package io.rocketbase.commons.service.client;

import io.rocketbase.commons.dto.appclient.AppClientWrite;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppClientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class DefaultAppClientService implements AppClientService {

    private final AppClientPersistenceService<AppClientEntity> clientPersistenceService;

    @Override
    public Page<AppClientEntity> findAll(QueryAppClient query, Pageable pageable) {
        return clientPersistenceService.findAll(query, pageable);
    }

    @Override
    public AppClientEntity create(AppClientWrite write) {
        AppClientEntity instance = clientPersistenceService.initNewInstance();
        return applyAndSave(write, instance);
    }

    @Override
    public AppClientEntity update(Long id, AppClientWrite write) {
        AppClientEntity entity = clientPersistenceService.findById(id).orElseThrow(NotFoundException::new);
        return applyAndSave(write, entity);
    }

    protected AppClientEntity applyAndSave(AppClientWrite write, AppClientEntity instance) {
        instance.setName(write.getName());
        instance.setSystemRefId(write.getSystemRefId());
        instance.setDescription(write.getDescription());
        instance.setCapabilityIds(write.getCapabilityIds());
        instance.setRedirectUrls(write.getRedirectUrls());
        return clientPersistenceService.save(instance);
    }

    @Override
    public Optional<AppClientEntity> findById(Long id) {
        return clientPersistenceService.findById(id);
    }

    @Override
    public void delete(Long id) {
        clientPersistenceService.delete(id);
    }
}
