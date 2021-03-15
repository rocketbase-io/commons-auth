package io.rocketbase.commons.service.client;

import io.rocketbase.commons.dto.appclient.AppClientWrite;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.model.AppClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppClientService {

    Page<AppClientEntity> findAll(QueryAppClient query, Pageable pageable);

    AppClientEntity create(AppClientWrite write);

    AppClientEntity update(Long id, AppClientWrite write);

    Optional<AppClientEntity> findById(Long id);

    /**
     * will delete client
     */
    void delete(Long id);
}
