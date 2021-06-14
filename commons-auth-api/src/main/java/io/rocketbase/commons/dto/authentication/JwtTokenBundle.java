package io.rocketbase.commons.dto.authentication;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rocketbase.commons.util.JwtTokenBody;
import io.rocketbase.commons.util.JwtTokenDecoder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * holds two tokens. one with short live-time for use and one to generate new short-tokens...
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "holds two tokens. one with short live-time for use and one to generate new short-tokens...")
public class JwtTokenBundle implements Serializable {

    /**
     * token with short live-time that is used for api-calls
     */
    @NotNull
    @Schema(description = "token with short live-time that is used for api-calls")
    private String token;

    /**
     * used to generate new tokens - could not be used for normal api-calls
     */
    @NotNull
    @Schema(description = "used to generate new tokens - could not be used for normal api-calls")
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

}
