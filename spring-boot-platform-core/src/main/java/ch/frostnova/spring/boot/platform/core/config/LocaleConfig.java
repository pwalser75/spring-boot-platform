package ch.frostnova.spring.boot.platform.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import static java.util.Locale.ENGLISH;

/**
 * Locale settings
 */
@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        // default language is english (for all localized error messages)
        slr.setDefaultLocale(ENGLISH);
        return slr;
    }
}
