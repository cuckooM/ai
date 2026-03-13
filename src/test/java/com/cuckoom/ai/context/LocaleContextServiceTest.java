package com.cuckoom.ai.context;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

import com.cuckoom.ai.config.AppProperties;

class LocaleContextServiceTest {

    @Test
    void testGetLocale_Chinese() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("zh");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        Locale locale = service.getLocale();
        // 验证语言是中文
        assertEquals("zh", locale.getLanguage());
    }

    @Test
    void testGetLocale_English() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("en");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        Locale locale = service.getLocale();
        assertEquals("en", locale.getLanguage());
    }

    @Test
    void testGetLocale_Japanese() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("ja");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        Locale locale = service.getLocale();
        assertEquals("ja", locale.getLanguage());
    }

    @Test
    void testGetLocale_French() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("fr");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        Locale locale = service.getLocale();
        assertEquals("fr", locale.getLanguage());
    }

    @Test
    void testGetLocale_TraditionalChinese() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("zh_tw");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        Locale locale = service.getLocale();
        assertEquals("zh", locale.getLanguage());
        assertEquals("TW", locale.getCountry());
    }

    @Test
    void testSetLanguage_SwitchToEnglish() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("zh");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        // 初始语言
        assertEquals("zh", service.getLocale().getLanguage());

        // 切换到英文
        service.setLanguage("en");
        assertEquals("en", service.getLocale().getLanguage());
    }

    @Test
    void testSetLanguage_SwitchToJapanese() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("en");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        // 切换到日文
        service.setLanguage("ja");
        assertEquals("ja", service.getLocale().getLanguage());
    }

    @Test
    void testSetLanguage_InsensitiveToCase() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("en");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        // 大小写不敏感
        service.setLanguage("ZH");
        assertEquals("zh", appProperties.getLanguage());

        service.setLanguage("JA");
        assertEquals("ja", appProperties.getLanguage());
    }

    @Test
    void testGetMessageWithArgs() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("en");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        // 测试带参数的消息
        String message = service.getMessageWithArgs("demorunner.error.unsupported-model", "test-model");
        assertNotNull(message);
        assertTrue(message.contains("test-model"));
    }

    @Test
    void testParseLocale_InvalidCode() {
        AppProperties appProperties = new AppProperties();
        appProperties.setLanguage("invalid");
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        LocaleContextService service = new LocaleContextService(messageSource, appProperties);

        // 无效语言代码应回退到中文
        Locale locale = service.getLocale();
        assertEquals("zh", locale.getLanguage());
    }
}
