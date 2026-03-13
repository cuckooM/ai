package com.cuckoom.ai.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiPropertiesTest {

    @Test
    void testDefaultModel() {
        AiProperties properties = new AiProperties();
        assertEquals("deepseek", properties.getDefaultModel());
    }

    @Test
    void testDeepseekModelProperties() {
        AiProperties properties = new AiProperties();
        assertNotNull(properties.getDeepseek());
    }

    @Test
    void testQwenModelProperties() {
        AiProperties properties = new AiProperties();
        assertNotNull(properties.getQwen());
    }

    @Test
    void testQwen3CoderNextModelProperties() {
        AiProperties properties = new AiProperties();
        assertNotNull(properties.getQwen3CoderNext());
    }

    @Test
    void testSetCustomModel() {
        AiProperties properties = new AiProperties();
        properties.setDefaultModel("qwen");
        assertEquals("qwen", properties.getDefaultModel());
    }

    @Test
    void testSetDeepseekConfig() {
        AiProperties properties = new AiProperties();
        properties.getDeepseek().setBaseUrl("https://api.deepseek.com");
        properties.getDeepseek().setApiKey("test-key");
        properties.getDeepseek().setModel("deepseek-reasoner");

        assertEquals("https://api.deepseek.com", properties.getDeepseek().getBaseUrl());
        assertEquals("test-key", properties.getDeepseek().getApiKey());
        assertEquals("deepseek-reasoner", properties.getDeepseek().getModel());
    }

    @Test
    void testSetQwenConfig() {
        AiProperties properties = new AiProperties();
        properties.getQwen().setBaseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1");
        properties.getQwen().setApiKey("test-qwen-key");
        properties.getQwen().setModel("qwen-max");

        assertEquals("https://dashscope.aliyuncs.com/compatible-mode/v1", properties.getQwen().getBaseUrl());
        assertEquals("test-qwen-key", properties.getQwen().getApiKey());
        assertEquals("qwen-max", properties.getQwen().getModel());
    }
}
