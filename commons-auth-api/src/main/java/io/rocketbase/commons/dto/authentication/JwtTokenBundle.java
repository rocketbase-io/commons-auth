package io.rocketbase.commons.dto.authentication;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rocketbase.commons.util.JwtTokenBody;
import io.rocketbase.commons.util.JwtTokenDecoder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenBundle implements Serializable {

    private String token;
    private String refreshToken;

    @JsonIgnore
    public Instant getAccessTokenExpiryDate() {
        JwtTokenBody body = JwtTokenDecoder.decodeTokenBody(token);
        return body.getExpiration();
    }

    @JsonIgnore
    public Instant getRefreshTokenExpiryDate() {
        JwtTokenBody body = JwtTokenDecoder.decodeTokenBody(refreshToken);
        return body.getExpiration();
    }

    public JwtTokenBundle(JwtTokenBundle other) {
        this.token = other.token;
        this.refreshToken = other.refreshToken;
    }
}
