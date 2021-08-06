package io.rocketbase.commons.model;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * interface to store audit events within commons-auth context
 */
public interface AuthAuditEntity extends Serializable {

    Long getId();

    void setId(Long id);

    Instant getTimestamp();

    void setTimestamp(@NotNull Instant timestamp);

    String getUserId();

    void setUserId(@NotNull
                   @Size(max = 36)
                           String userId);

    String getEventType();

    void setEventType(@NotNull
                      @Size(max = 12)
                              String eventType);

    String getEventDetail();

    void setEventDetail(@NotNull
                        @Size(max = 20)
                                String eventDetail);

    String getSource();

    void setSource(@NotNull
                   @Size(max = 40)
                           String source);

    String getIdentifier();

    /**
     * max it's a uuid or key of keyValue
     */
    void setIdentifier(@Nullable
                       @Size(max = 50)
                               String identifier);

}
