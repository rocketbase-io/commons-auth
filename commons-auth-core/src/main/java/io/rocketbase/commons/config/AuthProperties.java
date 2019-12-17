package io.rocketbase.commons.config;

import io.rocketbase.commons.util.UrlParts;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    @NotEmpty
    private String roleAdmin = "ADMIN";

    @NotEmpty
    private String roleUser = "USER";

    @NotEmpty
    private String tokenSecret = "E*iqzFiW#kSmAo8rO^V8%DRlQ#1f&B$i";

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
     * in minutes
     */
    private long passwordResetExpiration = 60;

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
                prefixPath + "auth/validate/*",
                prefixPath + "auth/register",
                prefixPath + "auth/verify"
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
                prefixPath + "auth/change-password"
        };
    }


    /**
     * quick help to configure spring security<br>
     * endpoint to crud users (normally only allowed for admins)
     */
    public String getApiRestEndpointPath() {
        return UrlParts.ensureStartsAndEndsWithSlash(prefix) + "api/user/*";
    }

    /**
     * quick help to configure spring security<br>
     * endpoint to search for users (normally allowed for all logged in users)
     */
    public String getUserSearchRestEndpointPath() {
        return UrlParts.ensureStartsAndEndsWithSlash(prefix) + "api/user-search";
    }
}
