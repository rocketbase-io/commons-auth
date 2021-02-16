package io.rocketbase.commons.config;

import io.rocketbase.commons.util.UrlParts;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * cache time in minutes <br>
     * 0 means disabled
     */
    private int userCacheTime = 30;

    private String baseUrl = "http://localhost:8080";

    /**
     * prefix for controllers
     */
    private String prefix = "";

    /**
     * should use verify it's email-adress
     */
    private boolean verifyEmail = true;

    /**
     * full qualified url to a custom UI that proceed the verification<br>
     * ?verification=VALUE will get append
     */
    private String verificationUrl = null;

    /**
     * full qualified url to a custom UI that proceed the password reset<br>
     * ?verification=VALUE will get append
     */
    private String passwordResetUrl = null;

    /**
     * full qualified url to a custom UI that proceed the invite reset<br>
     * ?inviteId=VALUE will get append
     */
    private String inviteUrl = null;

    /**
     * full qualified url to a custom UI that proceed the change email<br>
     * ?inviteId=VALUE will get append
     */
    private String changeEmailUrl = null;

    /**
     * full qualified url to a custom UI that proceed the change username<br>
     * ?inviteId=VALUE will get append
     */
    private String changeUsernameUrl = null;

    /**
     * in minutes
     */
    private long passwordResetExpiration = 60;

    /**
     * in minutes
     */
    private long changeEmailExpiration = 60;

    /**
     * in minutes
     */
    private long changeUsernameExpiration = 60;

    /**
     * in minutes - default 7 days
     */
    private long inviteExpiration = 10080;

    /**
     * quick help to configure login spring security<br>
     * endpoints login and oauth
     */
    public String getOauthRestEndpointPaths() {
        return UrlParts.ensureStartsAndEndsWithSlash(prefix) + "auth/oauth2/token";
    }

    /**
     * quick help to configure spring security<br>
     * endpoints like login, forgot password, registration etc
     */
    public String[] getAllPublicRestEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                getOauthRestEndpointPaths(),
                prefixPath + "auth/login",
                prefixPath + "auth/forgot-password",
                prefixPath + "auth/reset-password",
                prefixPath + "auth/validate",
                prefixPath + "auth/validate/*",
                prefixPath + "auth/register",
                prefixPath + "auth/invite",
                prefixPath + "auth/verify",
                prefixPath + "auth/verify-email"
        };
    }

    /**
     * quick help to configure spring security<br>
     * endpoints for logged in users to interact with their data
     */
    public String[] getAllAuthenticatedRestEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "auth/me",
                prefixPath + "auth/refresh",
                prefixPath + "auth/update-profile",
                prefixPath + "auth/update-setting",
                prefixPath + "auth/change-password",
                prefixPath + "auth/change-username",
                prefixPath + "auth/change-email"
        };
    }

    /**
     * quick help to configure spring security<br>
     * endpoint to crud users (normally only allowed for admins)
     */
    public String[] getApiRestEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "api/user",
                prefixPath + "api/user/*",
                prefixPath + "api/user/*/*"
        };
    }

    /**
     * quick help to configure spring security<br>
     * endpoint to crud invite (normally only allowed for admins)
     */
    public String[] getApiInviteRestEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "api/invite",
                prefixPath + "api/invite/*"
        };
    }

    /**
     * quick help to configure spring security<br>
     * endpoint to search for users (normally allowed for all logged in users)
     */
    public String[] getUserSearchRestEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "api/user-search",
                prefixPath + "api/user-search/*"
        };
    }

    /**
     * quick help to configure login spring security<br>
     * endpoints login and oauth
     */
    public String getImpersonateEndpointPaths() {
        return UrlParts.ensureStartsAndEndsWithSlash(prefix) + "api/impersonate/*";
    }
}
