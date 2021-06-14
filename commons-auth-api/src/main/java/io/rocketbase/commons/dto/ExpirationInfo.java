package io.rocketbase.commons.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;

/**
 * wrapper object related to expirations
 * @param <T> detailed object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "wrapper object related to expirations")
public class ExpirationInfo<T> implements Serializable {

    @Nullable
    private Instant expires;

    @Nullable
    private T detail;

    /**
     * duration in seconds after verification-link will expire
     */
    @Nullable
    @Schema(description = "duration in seconds after verification-link will expire")
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