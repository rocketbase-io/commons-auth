package io.rocketbase.commons.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

public interface AppClientEntity extends Serializable, EntityWithAudit, EntityWithSystemRefId {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(@NotNull @Size(min = 1, max = 100) String name);

    String getDescription();

    void setDescription(@Size(max = 500) String description);

    Set<Long> getCapabilityIds();

    void setCapabilityIds(Set<Long> capabilityIds);

    Set<String> getRedirectUrls();

    void setRedirectUrls(Set<String> redirectUrls);

}
