package io.rocketbase.commons.config;

import io.rocketbase.commons.util.UrlParts;
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
     */
    public String[] getFormEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "login",
                prefixPath + "logout",
                prefixPath + "forgot",
                prefixPath + "reset-password"
        };
    }

    /**
     * quick help to configure spring security
     */
    public String[] getInviteEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "invite"
        };
    }

    /**
     * quick help to configure spring security
     */
    public String[] getRegistrationEndpointPaths() {
        String prefixPath = UrlParts.ensureStartsAndEndsWithSlash(prefix);
        return new String[]{
                prefixPath + "registration",
                prefixPath + "verification"
        };
    }
}
