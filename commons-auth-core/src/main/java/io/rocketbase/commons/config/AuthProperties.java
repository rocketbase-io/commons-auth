package io.rocketbase.commons.config;

import io.rocketbase.commons.util.UrlParts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * 0 means disabled
     */
    @Builder.Default
    private Duration userCacheTime = Duration.of(30, ChronoUnit.MINUTES);

    @Builder.Default
    private String baseUrl = "http://localhost:8080";

    /**
     * prefix for controllers
     */
    @Builder.Default
    private String prefix = "";

    /**
     * should use verify it's email-adress
     */
    @Builder.Default
    private boolean verifyEmail = true;

    /**
     * full qualified url to a custom UI that proceed the verification<br>
     * ?verification=VALUE will get append
     */
    private String verificationUrl;

    /**
     * full qualified url to a custom UI that proceed the password reset<br>
     * ?verification=VALUE will get append
     */
    private String passwordResetUrl;

    /**
     * full qualified url to a custom UI that proceed the invite reset<br>
     * ?inviteId=VALUE will get append
     */
    private String inviteUrl;

    /**
     * full qualified url to a custom UI that proceed the change email<br>
     * ?inviteId=VALUE will get append
     */
    private String changeEmailUrl;

    /**
     * full qualified url to a custom UI that proceed the change username<br>
     * ?inviteId=VALUE will get append
     */
    private String changeUsernameUrl;

    @Builder.Default
    private Duration passwordResetExpiration = Duration.of(1, ChronoUnit.HOURS);

    @Builder.Default
    private Duration changeEmailExpiration = Duration.of(1, ChronoUnit.HOURS);

    @Builder.Default
    private Duration changeUsernameExpiration = Duration.of(1, ChronoUnit.HOURS);

    @Builder.Default
    private Duration inviteExpiration = Duration.of(7, ChronoUnit.DAYS);

    /**
     * quick help to configure login spring security<br>
     * endpoints login and oauth
     */
    public String[] getOauthRestEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "oauth/auth",
                prefixPath + "oauth/token"
        };
    }

    /**
     * quick help to configure spring security<br>
     * endpoints like login, forgot password, registration etc
     */
    public String[] getAllPublicRestEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "oauth/auth",
                prefixPath + "oauth/token",
                prefixPath + "oauth/.well-known/openid-configuration",
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
