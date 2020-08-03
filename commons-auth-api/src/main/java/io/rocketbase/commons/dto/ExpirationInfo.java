package io.rocketbase.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationInfo<T> implements Serializable {

    @Nullable
    private Instant expires;

    @Nullable
    private T detail;

    /**
     * duration in seconds after verification-link will expire
     */
    @Nullable
    public Long getExpiresAfter() {
        if (expires == null) {
            return null;
        }
        return Instant.now().getEpochSecond() - expires.getEpochSecond();
    }

    public boolean isExpired() {
        return expires != null && Instant.now().isAfter(expires);
    }

}