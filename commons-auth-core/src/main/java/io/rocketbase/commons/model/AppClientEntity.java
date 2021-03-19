package io.rocketbase.commons.model;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

public interface AppClientEntity extends Serializable {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(@NotNull
                 @Size(min = 1, max = 100)
                         String name);

    String getSystemRefId();

    void setSystemRefId(@Nullable
                        @Size(max = 100)
                                String systemRefId);

    String getDescription();

    void setDescription(@Size(max = 500)
                                String description);

    Set<Long> getCapabilityIds();

    void setCapabilityIds(Set<Long> capabilityIds);

    Set<String> getRedirectUrls();

    void setRedirectUrls(Set<String> redirectUrls);

    Instant getCreated();

    Instant getModified();

    String getModifiedBy();

}
