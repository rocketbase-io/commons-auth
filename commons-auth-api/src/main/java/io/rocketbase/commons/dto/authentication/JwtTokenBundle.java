package io.rocketbase.commons.dto.authentication;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rocketbase.commons.util.JwtTokenDecoder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenBundle implements Serializable {

    private String token;
    private String refreshToken;

    @JsonIgnore
    public LocalDateTime getAccessTokenExpiryDate() {
        JwtTokenDecoder.JwtTokenBody body = JwtTokenDecoder.decodeTokenBody(token);
        return body.getExpiration();
    }

    @JsonIgnore
    public LocalDateTime getRefreshTokenExpiryDate() {
        JwtTokenDecoder.JwtTokenBody body = JwtTokenDecoder.decodeTokenBody(refreshToken);
        return body.getExpiration();
    }

}
