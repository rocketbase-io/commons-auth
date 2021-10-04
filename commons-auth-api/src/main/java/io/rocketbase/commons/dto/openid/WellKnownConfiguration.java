package io.rocketbase.commons.dto.openid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WellKnownConfiguration {

    @JsonProperty("issuer")
    private String issuer;

    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;

    @JsonProperty("token_endpoint")
    private String tokenEndpoint;

    @JsonProperty("userinfo_endpoint")
    private String userinfoEndpoint;

    @JsonProperty("registration_endpoint")
    private String registrationEndpoint;

    @JsonProperty("response_types_supported")
    private Set<String> responseTypesSupported;

    @JsonProperty("response_modes_supported")
    private Set<String> responseModesSupported;

    @JsonProperty("grant_types_supported")
    private Set<String> grantTypesSupported;

    @JsonProperty("scopes_supported")
    private Set<String> scopesSupported;

    @JsonProperty("claims_supported")
    private Set<String> claimsSupported;

    @JsonProperty("revocation_endpoint")
    private String revocationEndpoint;

    @JsonProperty("end_session_endpoint")
    private String endSessionEndpoint;
}
