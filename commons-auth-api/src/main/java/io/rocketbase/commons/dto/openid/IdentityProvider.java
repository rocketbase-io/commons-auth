package io.rocketbase.commons.dto.openid;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "key")
public class IdentityProvider implements Serializable {

    private String key;

    private String name;

    private String description;

    private String logo;

    private Endpoints endpoints;

    private String clientId;

    private String clientSecret;

    private String scope;

    private boolean defaultProvider = false;

    /**
     * JSON Web Key Sets
     */
    private String jwksUri;

    @Data
    public static class Endpoints {

        private String issuer;
        private String authorization;
        private String token;
        private String userinfo;
        private String revocation;
        private String endSession;

    }
}
