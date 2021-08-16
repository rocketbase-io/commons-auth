package io.rocketbase.commons.dto.authentication.oauth;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * User for multiple flows<br>
 * - Authorization Code Flow (grant_type=grant_type): will be called with filled code and redirect_uri<br>
 * - Resource Owner Password Credentials Flow (grant_type=password): will be called with filled username, password<br>
 * - Refresh Token Flow (grant_type=refresh_token): will be called with refresh_token and scope
 */
@Data
public class TokenRequest {

    /**
     * Can be one of the following: authorization_code, password, client_credentials, refresh_token
     */
    @NotNull
    private String grant_type;

    /**
     * Required if grant_type is authorization_code. The value is what was returned from the authorization endpoint. The code has a lifetime of 300 seconds
     */
    private String code;

    /**
     * Required if grant_type is authorization_code. Specifies the callback location where the authorization was sent. This value must match the redirect_uri used to generate the original authorization_code.
     */
    private String redirect_uri;

    /**
     * Required if the grant_type is password.
     */
    private String username;
    /**
     * Required if the grant_type is password.
     */
    private String password;

    /**
     * Required if grant_type is refresh_token. The value is a valid refresh token that was returned from this endpoint previously.
     */
    private String refresh_token;

    /**
     * Required if password is the grant_type. This is a list of scopes that the client wants to be included in the access token. For the refresh_token grant type, these scopes have to be a subset of the scopes used to generate the refresh token in the first place.
     */
    private String scope;
}
