package io.rocketbase.commons.vaadin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuthVaadinProperties.class})
@RequiredArgsConstructor
public class AuthVaadinAutoConfiguration {

    private final AuthVaadinProperties authVaadinProperties;

}
