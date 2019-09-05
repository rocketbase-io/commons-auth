package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth.forms")
public class FormsProperties {

    /**
     * prefix for the paths of the forms-controller
     */
    private String prefix = "";

    private String title = "commons-auth";

    private String logoSrc = "./assets/rocketbase.svg";

    /**
     * quick help to configure spring security
     *
     * @param prefix in case you've set a formsProperties.prefix
     */
    public static String[] getAllPublicFormEndpointPaths(String prefix) {
        return new String[]{
                prefix + "/login",
                prefix + "/logout",
                prefix + "/forgot",
                prefix + "/reset-password",
                prefix + "/registration",
                prefix + "/verify"
        };
    }
}
