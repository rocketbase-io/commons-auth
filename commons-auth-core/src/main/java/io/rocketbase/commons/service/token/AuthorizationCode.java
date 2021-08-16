package io.rocketbase.commons.service.token;

import io.rocketbase.commons.dto.authentication.oauth.AuthRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizationCode implements Serializable {

    private String code;

    private AuthRequest authRequest;

    private String userId;

    private Instant invalid;

    public AuthorizationCode(AuthRequest authRequest, String userId, long invalidAfterSeconds) {
        this.code = generateCode();
        this.authRequest = authRequest;
        this.userId = userId;
        this.invalid = buildInvalid(invalidAfterSeconds);
    }

    public static AuthorizationCode copyConfig(AuthorizationCode other, long invalidAfterSeconds) {
        return AuthorizationCode.builder()
                .code(generateCode())
                .authRequest(other.getAuthRequest())
                .userId(other.getUserId())
                .invalid(buildInvalid(invalidAfterSeconds))
                .build();
    }

    protected static String generateCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    protected static Instant buildInvalid(long invalidAfterSeconds) {
        return Instant.now().plusSeconds(invalidAfterSeconds);
    }

    public boolean isValid() {
        return Instant.now().isBefore(invalid);
    }

    public String buildRedirectUri() {
        return UriComponentsBuilder.fromUriString(authRequest.getRedirect_uri())
                .queryParam("code", getCode())
                .queryParam("state", authRequest.getState())
                .toUriString();
    }
}
