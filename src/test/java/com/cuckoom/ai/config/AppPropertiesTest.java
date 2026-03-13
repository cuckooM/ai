package com.cuckoom.ai.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppPropertiesTest {

    @Test
    void testDefaultLanguage() {
        AppProperties properties = new AppProperties();
        assertEquals("zh", properties.getLanguage());
    }

    @Test
    void testSetLanguage() {
        AppProperties properties = new AppProperties();
        properties.setLanguage("en");
        assertEquals("en", properties.getLanguage());
    }

    @Test
    void testSetLanguage_IgnoreCase() {
        AppProperties properties = new AppProperties();
        properties.setLanguage("ZH");
        assertEquals("ZH", properties.getLanguage());
    }

    @Test
    void testSetLanguage_Null() {
        AppProperties properties = new AppProperties();
        properties.setLanguage(null);
        assertNull(properties.getLanguage());
    }

    @Test
    void testSetLanguage_Empty() {
        AppProperties properties = new AppProperties();
        properties.setLanguage("");
        assertEquals("", properties.getLanguage());
    }
}
