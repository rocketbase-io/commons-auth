package io.rocketbase.commons.model;

import org.springframework.lang.Nullable;

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

    @NotNull
    void setTimestamp(Instant timestamp);

    String getUserId();

    @NotNull
    @Size(max = 36)
    void setUserId(String userId);

    String getEventType();

    @NotNull
    @Size(max = 12)
    void setEventType(String eventType);

    String getEventDetail();

    @NotNull
    @Size(max = 20)
    void setEventDetail(String eventDetail);

    String getSource();

    @NotNull
    @Size(max = 40)
    void setSource(String source);

    String getIdentifier();

    /**
     * max it's a uuid or key of keyValue
     */
    @Nullable
    @Size(max = 50)
    void setIdentifier(String identifier);

}
