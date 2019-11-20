package io.rocketbase.commons.config;

import io.rocketbase.commons.util.UrlParts;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * quick help to configure login spring security
     *
     * @param prefix in case you've set a prefix
     */
    public static String[] getAuthRestEndpointPaths(String prefix) {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "auth/login",
                prefixPath + "auth/oauth2/token"
        };
    }

    /**
     * quick help to configure spring security
     *
     * @param prefix in case you've set a prefix
     */
    public static String[] getAllPublicRestEndpointPaths(String prefix) {
        // normal login
        List<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(getAllAuthenticatedRestEndpointPaths(prefix)));
        // rest endpoints
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        result.add(prefixPath + "auth/forgot-password");
        result.add(prefixPath + "auth/reset-password");
        result.add(prefixPath + "auth/validate/*");
        result.add(prefixPath + "auth/register");
        result.add(prefixPath + "auth/verify");
        return result.toArray(new String[]{});
    }

    /**
     * quick help to configure spring security
     *
     * @param prefix in case you've set a prefix
     */
    public static String[] getAllAuthenticatedRestEndpointPaths(String prefix) {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "auth/refresh",
                prefixPath + "auth/update-profile",
                prefixPath + "auth/change-password"
        };
    }

    /**
     * quick help to configure spring security
     *
     * @param prefix in case you've set a prefix
     */
    public static String[] getAllApiRestEndpointPaths(String prefix) {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "api/user-search/*",
                prefixPath + "api/user/*"
        };
    }
}
