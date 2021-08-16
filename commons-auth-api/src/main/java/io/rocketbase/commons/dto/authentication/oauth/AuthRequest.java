package io.rocketbase.commons.dto.authentication.oauth;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * used in oauth context for mainly the "Authorization Code Flow"<br>
 * this is the first request that will answer with an redirect
 */
@Data
public class AuthRequest {

    /**
     * Obtained during either manual client registration or via the Dynamic Client Registration API. It identifies the client and must match the value preregistered in Okta.
     */
    private String client_id;

    /**
     * The display parameter to be passed to the Social Identity Provider when performing Social Login.
     */
    private String display;

    /**
     * A username to prepopulate if prompting for authentication.
     */
    private String login_hint;

    /**
     * Allowable elapsed time, in seconds, since the last time the end user was actively authenticated.
     */
    private Integer max_age;

    /**
     * Callback location where the authorization code or tokens should be sent. It must match the value preregistered in Okta during client registration.
     */
    @NotNull
    private String redirect_uri;

    /**
     * Any combination of code, token, and id_token. The combination determines the flow.
     */
    private String response_type;

    /**
     * openid is required for authentication requests. Other scopes may also be included.
     */
    private String scope;

    /**
     * A value to be returned in the token. The client application can use it to remember the state of its interaction with the end user at the time of the authentication call. It can contain alphanumeric, comma, period, underscore, and hyphen characters. See Parameter details.
     */
    private String state;
}
