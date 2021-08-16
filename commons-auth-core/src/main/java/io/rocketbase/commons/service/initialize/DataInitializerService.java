package io.rocketbase.commons.service.initialize;

import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.model.AppUserEntity;

public interface DataInitializerService {

    /**
     * check if user is present in db or create ist
     *
     * @param create   instructions for case of creation
     * @param keyPaths will check each keyPath existence and add these to users rights
     * @return found or created entity
     */
    AppUserEntity checkUserInitialized(AppUserCreate create, String... keyPaths);

    /**
     * check if total keyPath is present in db or create missing
     *
     * @param keyPath could check also multiple capabilities via dots
     * @return final Capability of the last keyPath segment
     */
    AppCapabilityEntity checkCapabilityKeyPathInitialized(String keyPath);
}
