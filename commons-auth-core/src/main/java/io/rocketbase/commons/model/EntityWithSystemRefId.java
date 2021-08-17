package io.rocketbase.commons.model;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;

public interface EntityWithSystemRefId {

    @Nullable
    String getSystemRefId();

    void setSystemRefId(@Size(max = 100) String systemRefId);
}