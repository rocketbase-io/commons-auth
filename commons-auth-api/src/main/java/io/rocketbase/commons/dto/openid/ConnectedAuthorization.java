package io.rocketbase.commons.dto.openid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"clientId"})
@Data
public class ConnectedAuthorization implements Serializable {

    /**
     * a mobile app for example an a desktop app should use different clientIds to support separated auth flows/states<br>
     * an AppUserEntity could hold a set of unique clientId values
     */
    private String clientId;

    /**
     * timestamp of expiration of the refreshToken to know if it's could still be valid to use it...
     */
    private Instant refreshExpires;

    private String refreshToken;

    @Transient
    public boolean isValid() {
        return refreshExpires != null && refreshExpires.isAfter(Instant.now());
    }

}
