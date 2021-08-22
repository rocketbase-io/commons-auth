package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.authentication.oauth.AuthRequest;
import io.rocketbase.commons.service.token.AuthorizationCode;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;


@Document(collection = "${auth.entity.prefix:co_}authcode")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "code")
public class AuthorizationCodeMongoEntity {

    @Id
    private String code;

    private String userId;

    private AuthRequest authRequest;

    @NotNull
    private Instant invalid;

    @Transient
    public AuthorizationCode toDto() {
        return AuthorizationCode.builder()
                .code(code)
                .userId(userId)
                .authRequest(authRequest)
                .invalid(invalid)
                .build();
    }

    public AuthorizationCodeMongoEntity(AuthorizationCode code) {
        this.code = code.getCode();
        this.userId = code.getUserId();
        this.authRequest = code.getAuthRequest();
        this.invalid = code.getInvalid();
    }
}
