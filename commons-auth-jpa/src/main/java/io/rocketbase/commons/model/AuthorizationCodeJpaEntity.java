package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.authentication.oauth.AuthRequest;
import io.rocketbase.commons.model.converter.AuthRequestConverter;
import io.rocketbase.commons.service.token.AuthorizationCode;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "co_authcode",
        indexes = {
                @Index(name = "idx_authcode_invalid", columnList = "invalid"),
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "code")
public class AuthorizationCodeJpaEntity {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Lob
    @Column(name = "redirect_url_json")
    @Convert(converter = AuthRequestConverter.class)
    private AuthRequest authRequest;

    @NotNull
    @Column(name = "invalid")
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

    public AuthorizationCodeJpaEntity(AuthorizationCode code) {
        this.code = code.getCode();
        this.userId = code.getUserId();
        this.authRequest = code.getAuthRequest();
        this.invalid = code.getInvalid();
    }
}
