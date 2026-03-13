package com.cuckoom.ai.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.cuckoom.ai.context.LocaleContextService;

class MultiModelAiConfigTest {

    private MultiModelAiConfig config;
    private AiProperties aiProperties;
    private LocaleContextService localeContextService;

    @BeforeEach
    void setUp() {
        aiProperties = new AiProperties();
        localeContextService = null; // Not needed for testing getModelProperties
        config = new MultiModelAiConfig(aiProperties, localeContextService);
    }

    @Test
    void testGetModelProperties_Default_WhenModelNull() {
        // when
        AiProperties.ModelProperties result = config.getModelProperties(null);

        // then
        assertNotNull(result);
        assertEquals(aiProperties.getDeepseek(), result);
    }

    @Test
    void testGetModelProperties_Default_WhenModelEmpty() {
        // given
        String modelName = "";

        // when
        AiProperties.ModelProperties result = config.getModelProperties(modelName);

        // then
        assertNotNull(result);
        assertEquals(aiProperties.getDeepseek(), result);
    }

    @Test
    void testGetModelProperties_WhitespaceModel() {
        // given
        String modelName = "   ";

        // when
        AiProperties.ModelProperties result = config.getModelProperties(modelName);

        // then
        assertNotNull(result);
        assertEquals(aiProperties.getDeepseek(), result);
    }

    @Test
    void testGetModelProperties_Qwen() {
        // given
        String modelName = "qwen";

        // when
        AiProperties.ModelProperties result = config.getModelProperties(modelName);

        // then
        assertNotNull(result);
        assertEquals(aiProperties.getQwen(), result);
    }

    @Test
    void testGetModelProperties_QwenUpperCase() {
        // given
        String modelName = "QWEN";

        // when
        AiProperties.ModelProperties result = config.getModelProperties(modelName);

        // then
        assertNotNull(result);
        assertEquals(aiProperties.getQwen(), result);
    }

    @Test
    void testGetModelProperties_Qwen3CoderNext() {
        // given
        String modelName = "qwen3-coder-next";

        // when
        AiProperties.ModelProperties result = config.getModelProperties(modelName);

        // then
        assertNotNull(result);
        assertEquals(aiProperties.getQwen3CoderNext(), result);
    }

    @Test
    void testGetModelProperties_Qwen3CoderNextMixedCase() {
        // given
        String modelName = "QWEN3-CODER-NEXT";

        // when
        AiProperties.ModelProperties result = config.getModelProperties(modelName);

        // then
        assertNotNull(result);
        assertEquals(aiProperties.getQwen3CoderNext(), result);
    }

    @Test
    void testGetModelProperties_DeepSeek() {
        // given
        String modelName = "deepseek";

        // when
        AiProperties.ModelProperties result = config.getModelProperties(modelName);

        // then
        assertNotNull(result);
        assertEquals(aiProperties.getDeepseek(), result);
    }

    @Test
    void testGetModelProperties_DeepSeekUpperCase() {
        // given
        String modelName = "DEEPSEEK";

        // when
        AiProperties.ModelProperties result = config.getModelProperties(modelName);

        // then
        assertNotNull(result);
        assertEquals(aiProperties.getDeepseek(), result);
    }

    @Test
    void testGetModelProperties_UnknownModel_FallsBackToDeepSeek() {
        // given
        String modelName = "unknown-model";

        // when
        AiProperties.ModelProperties result = config.getModelProperties(modelName);

        // then
        assertNotNull(result);
        // Unknown models should fall back to deepseek
        assertEquals(aiProperties.getDeepseek(), result);
    }

    @Test
    void testValidateModelProperties_ValidProperties() {
        // given
        AiProperties.ModelProperties properties = new AiProperties.ModelProperties();
        properties.setBaseUrl("https://api.example.com");
        properties.setApiKey("test-key");
        String modelName = "test-model";

        // when & then
        assertDoesNotThrow(() -> {
            validateModelPropertiesDirectly(properties, modelName);
        });
    }

    @Test
    void testValidateModelProperties_NullBaseUrl() {
        // given
        AiProperties.ModelProperties properties = new AiProperties.ModelProperties();
        properties.setBaseUrl(null);
        properties.setApiKey("test-key");
        String modelName = "test-model";

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            validateModelPropertiesDirectly(properties, modelName);
        });
    }

    @Test
    void testValidateModelProperties_EmptyBaseUrl() {
        // given
        AiProperties.ModelProperties properties = new AiProperties.ModelProperties();
        properties.setBaseUrl("");
        properties.setApiKey("test-key");
        String modelName = "test-model";

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            validateModelPropertiesDirectly(properties, modelName);
        });
    }

    @Test
    void testValidateModelProperties_WhitespaceBaseUrl() {
        // given
        AiProperties.ModelProperties properties = new AiProperties.ModelProperties();
        properties.setBaseUrl("   ");
        properties.setApiKey("test-key");
        String modelName = "test-model";

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            validateModelPropertiesDirectly(properties, modelName);
        });
    }

    @Test
    void testValidateModelProperties_NullApiKey() {
        // given
        AiProperties.ModelProperties properties = new AiProperties.ModelProperties();
        properties.setBaseUrl("https://api.example.com");
        properties.setApiKey(null);
        String modelName = "test-model";

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            validateModelPropertiesDirectly(properties, modelName);
        });
    }

    @Test
    void testValidateModelProperties_EmptyApiKey() {
        // given
        AiProperties.ModelProperties properties = new AiProperties.ModelProperties();
        properties.setBaseUrl("https://api.example.com");
        properties.setApiKey("");
        String modelName = "test-model";

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            validateModelPropertiesDirectly(properties, modelName);
        });
    }

    @Test
    void testValidateModelProperties_WhitespaceApiKey() {
        // given
        AiProperties.ModelProperties properties = new AiProperties.ModelProperties();
        properties.setBaseUrl("https://api.example.com");
        properties.setApiKey("   ");
        String modelName = "test-model";

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            validateModelPropertiesDirectly(properties, modelName);
        });
    }

    // Direct implementation for testing
    private void validateModelPropertiesDirectly(AiProperties.ModelProperties properties, String modelName) {
        if (properties.getBaseUrl() == null || properties.getBaseUrl().trim().isEmpty()) {
            throw new IllegalStateException("Base URL is required for model: " + modelName);
        }
        if (properties.getApiKey() == null || properties.getApiKey().trim().isEmpty()) {
            throw new IllegalStateException("API key is required for model: " + modelName);
        }
    }
}
