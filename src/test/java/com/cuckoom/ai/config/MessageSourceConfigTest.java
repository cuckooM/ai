package com.cuckoom.ai.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

import com.cuckoom.ai.context.LocaleContextService;

class MessageSourceConfigTest {

    @Test
    void testMessageSourceBeanCreation() {
        // when
        MessageSource messageSource = createMessageSource();

        // then
        assertNotNull(messageSource);
    }

    @Test
    void testMessageSource_Basename() {
        // given
        MessageSource messageSource = createMessageSource();

        // when
        String message = messageSource.getMessage("demorunner.info.exit", null, null);

        // then
        assertNotNull(message);
    }

    @Test
    void testMessageSource_Encoding() {
        // given
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());

        // when
        String message = messageSource.getMessage("demorunner.info.exit", null, null);

        // then
        assertNotNull(message);
    }

    @Test
    void testLocaleContextService_Creation() {
        // given
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        AppProperties appProperties = new AppProperties();

        // when
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        // then
        assertNotNull(service);
    }

    @Test
    void testLocaleContextService_GetLocale() {
        // given
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("en");

        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        // when
        var locale = service.getLocale();

        // then
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
    }

    @Test
    void testLocaleContextService_SetLanguage() {
        // given
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        AppProperties appProperties = new AppProperties();

        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        // when
        service.setLanguage("ja");
        var locale = service.getLocale();

        // then
        assertNotNull(locale);
        assertEquals("ja", locale.getLanguage());
    }

    // Helpers
    private MessageSource createMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }
}
