package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.dto.appclient.AppClientWrite;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppClientApi {

    default PageableResult<AppClientRead> find(Pageable pageable) {
        return find(null, pageable);
    }

    PageableResult<AppClientRead> find(QueryAppClient query, Pageable pageable);

    Optional<AppClientRead> findById(Long id);

    AppClientRead create(AppClientWrite write);

    AppClientRead update(Long id, AppClientWrite write);

    void delete(Long id);
}