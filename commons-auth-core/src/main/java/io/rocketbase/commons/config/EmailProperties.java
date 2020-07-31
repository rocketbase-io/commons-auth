package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth.email")
public class EmailProperties {

    private String subjectPrefix = "[Auth]";
    private String serviceName = "commons-auth";

    private EmailLogo logo;
    private String greetingFrom = "commons-auth";

    private String supportEmail = "support@localhost";
    private String fromEmail = "no-reply@localhost";
    private String copyrightName = "commons-auth";
    private String copyrightUrl = "https://github.com/rocketbase-io/commons-auth";

    @Data
    public static class EmailLogo {
        private String src;
        private Integer width;
        private Integer height;
    }

}
