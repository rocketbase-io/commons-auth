package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppGroupApi {

    default PageableResult<AppGroupRead> find(Pageable pageable) {
        return find(null, pageable);
    }

    PageableResult<AppGroupRead> find(QueryAppGroup query, Pageable pageable);

    Optional<AppGroupRead> findById(Long id);

    AppGroupRead create(Long parentId, AppGroupWrite write);

    AppGroupRead update(Long id, AppGroupWrite write);

    void delete(Long id);
}
