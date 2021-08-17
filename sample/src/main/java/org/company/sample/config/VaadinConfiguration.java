package org.company.sample.config;

import com.vaadin.flow.i18n.I18NProvider;
import io.rocketbase.commons.vaadin.i18n.TranslationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class VaadinConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public I18NProvider i18NProvider(@Autowired ResourceLoader resourceLoader) {
        return new TranslationProvider(resourceLoader);
    }
}
