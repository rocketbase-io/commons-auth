package io.rocketbase.commons.service.client;

import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.model.AppClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppClientService {

    Page<AppClientRead> findAll(QueryAppClient query, Pageable pageable);

    AppClientEntity save(AppClientEntity entity);

    Optional<AppClientEntity> findById(Long id);

    /**
     * will delete client
     */
    void delete(Long id);
}
