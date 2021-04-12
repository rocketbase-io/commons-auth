package io.rocketbase.commons.vaadin.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class TranslationProvider implements I18NProvider, InitializingBean {
    private final ResourceLoader resourceLoader;

    @Getter
    private final List<Locale> providedLocales = new ArrayList<>();

    private ReloadableResourceBundleMessageSource messageSource;

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        try {
            return messageSource.getMessage(key, params, locale);
        } catch (NoSuchMessageException e) {
        }

        log.warn("Key: {} not found in bundle", key);
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        registerLocale(Locale.ROOT);

        try {
            Resource[] i18nFiles = ResourcePatternUtils
                    .getResourcePatternResolver(resourceLoader)
                    .getResources("classpath*:i18n/*.properties");

            messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setDefaultEncoding("UTF-8");
            messageSource.setFallbackToSystemLocale(false);
            messageSource.setUseCodeAsDefaultMessage(true);
            messageSource.setDefaultLocale(Locale.ROOT);

            Set<String> knownFilenames = new HashSet<>();

            for (Resource file : i18nFiles) {
                String filename = file.getFilename();
                if (filename == null)
                    continue;
                filename = filename.substring(0, filename.length() - 11);
                try {
                    String bundleName;

                    if (filename.contains("_")) {
                        int split = filename.lastIndexOf('_');
                        bundleName = filename.substring(0, split);
                        registerLocale(Locale.forLanguageTag(filename.substring(split + 1)));
                    } else {
                        bundleName = filename;
                    }

                    if (!knownFilenames.contains(bundleName)) {
                        messageSource.addBasenames("i18n/" + bundleName);
                        knownFilenames.add(bundleName);
                    }
                } catch (Exception e) {
                    log.error("problems with i18n file '{}': {}", filename, e);
                }
            }
        } catch (Exception e) {
            log.error("error reading i18n properties:", e);
        }
    }

    private void registerLocale(Locale locale) {
        if (!providedLocales.contains(locale))
            providedLocales.add(locale);
    }
}
