package io.rocketbase.commons.model;

import java.time.Instant;

public interface EntityWithAudit {

    Instant getCreated();

    void setCreated(Instant created);

    Instant getModified();

    String getModifiedBy();
}
