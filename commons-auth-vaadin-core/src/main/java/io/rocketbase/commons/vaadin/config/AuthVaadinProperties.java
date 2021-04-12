package io.rocketbase.commons.vaadin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth.vaadin")
public class AuthVaadinProperties {

    private String timezone = "CET";

}
