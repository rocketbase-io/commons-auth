package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import org.springframework.data.domain.Pageable;

public interface AppGroupApi {

    default PageableResult<AppGroupRead> find(Pageable pageable) {
        return find(null, pageable);
    }

    PageableResult<AppGroupRead> find(QueryAppGroup query, Pageable pageable);

    AppGroupRead create(Long parentId, AppGroupWrite write);

    AppGroupRead update(Long id, AppGroupWrite write);

    void delete(String id);
}
