package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

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

    protected static String transferPrefix(String prefix) {
        String result = "";
        if (!StringUtils.isEmpty(prefix) && !prefix.equals("/")) {
            result = prefix;
            if (result.endsWith("/")) {
                result = result.substring(0, result.length() - 1);
            }
            if (!result.startsWith("/")) {
                result = "/" + result;
            }
        }
        return result;
    }

    /**
     * quick help to configure spring security
     *
     * @param prefix in case you've set a prefix
     */
    public static String[] getAllPublicRestEndpointPaths(String prefix) {
        String prefixPath = transferPrefix(prefix);
        return new String[]{
                prefixPath + "/auth/login",
                prefixPath + "/auth/oauth2/token",
                prefixPath + "/auth/forgot-password",
                prefixPath + "/auth/reset-password",
                prefixPath + "/auth/validate/*",
                prefixPath + "/auth/register",
                prefixPath + "/auth/verify"
        };
    }

    /**
     * quick help to configure spring security
     *
     * @param prefix in case you've set a prefix
     */
    public static String[] getAllAuthenticatedRestEndpointPaths(String prefix) {
        String prefixPath = transferPrefix(prefix);
        return new String[]{
                prefixPath + "/auth/refresh",
                prefixPath + "/auth/update-profile",
                prefixPath + "/auth/change-password"
        };
    }

    /**
     * quick help to configure spring security
     *
     * @param prefix in case you've set a prefix
     */
    public static String[] getAllApiRestEndpointPaths(String prefix) {
        String prefixPath = transferPrefix(prefix);
        return new String[]{
                prefixPath + "/api/user-search/*",
                prefixPath + "/api/user/*"
        };
    }
}
