package io.rocketbase.commons.config;

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
     * quick help to configure spring security
     *
     * @param prefix in case you've set a prefix
     */
    public static String[] getAllPublicRestEndpointPaths(String prefix) {
        return new String[]{
                prefix + "/auth/login",
                prefix + "/auth/refresh",
                prefix + "/auth/forgot-password",
                prefix + "/auth/reset-password",
                prefix + "/auth/validate/*",
                prefix + "/auth/register",
                prefix + "/auth/verify"
        };
    }
}
