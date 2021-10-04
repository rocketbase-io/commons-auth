package io.rocketbase.commons.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties(prefix = "auth.email")
public class EmailProperties {

    @Builder.Default
    private String subjectPrefix = "[Auth]";

    @Builder.Default
    private String serviceName = "commons-auth";

    private EmailLogo logo;

    @Builder.Default
    private String greetingFrom = "commons-auth";

    @Builder.Default
    private String supportEmail = "support@localhost";

    @Builder.Default
    private String fromEmail = "no-reply@localhost";

    @Builder.Default
    private String copyrightName = "commons-auth";

    @Builder.Default
    private String copyrightUrl = "https://github.com/rocketbase-io/commons-auth";

    @Data
    public static class EmailLogo {
        private String src;
        private Integer width;
        private Integer height;
    }

}
