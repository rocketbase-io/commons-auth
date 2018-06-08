package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "auth.email")
public class EmailProperties {

    private String subjectPrefix = "[Auth]";
    private String serviceName = "commons-auth";
    private String supportEmail = "support@localhost";
    private String fromEmail = "no-reply@localhost";
    private String copyrightName = "commons-auth";
    private String copyrightUrl = "https://github.com/rocketbase-io/commons-auth";

}
