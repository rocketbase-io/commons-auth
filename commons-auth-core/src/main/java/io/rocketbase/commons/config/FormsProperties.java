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

}
