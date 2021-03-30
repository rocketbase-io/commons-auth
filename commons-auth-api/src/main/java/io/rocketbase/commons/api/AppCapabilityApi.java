package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import org.springframework.data.domain.Pageable;

public interface AppCapabilityApi {

    default PageableResult<AppCapabilityRead> find(Pageable pageable) {
        return find(null, pageable);
    }

    PageableResult<AppCapabilityRead> find(QueryAppCapability query, Pageable pageable);

    AppCapabilityRead create(Long parentId, AppCapabilityWrite write);

    AppCapabilityRead update(Long id, AppCapabilityWrite write);

    void delete(String id);
}
