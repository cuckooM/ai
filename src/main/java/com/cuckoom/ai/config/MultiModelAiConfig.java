package com.cuckoom.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.cuckoom.ai.context.LocaleContextService;

/**
 * 多模型 AI 配置类
 * <p>
 * 该配置类负责创建和管理所有 AI 模型的 ChatClient Bean。
 * 支持 DeepSeek、通义千问（Qwen）和 Qwen3 Coder Next 模型。
 * </p>
 * <p>
 * 配置方式：
 * <pre>{@code
 * spring.ai:
 *   default-model: deepseek
 *   deepseek:
 *     enabled: true
 *     base-url: https://api.deepseek.com
 *     api-key: ${DEEPSEEK_API_KEY:your_key}
 *     model: deepseek-chat
 *   qwen:
 *     enabled: true
 *     base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
 *     api-key: ${QWEN_API_KEY:your_key}
 *     model: qwen-plus
 * }</pre>
 * </p>
 *
 * @author Cuckoom
 */
@Configuration
@RequiredArgsConstructor
public class MultiModelAiConfig {

    /**
     * AI 配置属性对象
     * <p>
     * 包含所有模型的配置信息，通过 constructor 注入。
     * </p>
     */
    private final AiProperties aiProperties;

    /**
     * 语言上下文服务
     * <p>
     * 统一的语言服务，用于获取多语言错误消息。
     * </p>
     */
    private final LocaleContextService localeContextService;

    /**
     * 创建默认 ChatClient Bean
     * <p>
     * 使用 {@code @Primary} 注解标记为首选 Bean，
     * 当没有指定Qualifier时，将使用此客户端。
     * </p>
     * <p>
     * 默认模型通过 {@code spring.ai.default-model} 配置属性决定。
     * </p>
     *
     * @return 默认的 ChatClient 实例
     * @throws IllegalStateException 当 API 基础 URL 或 API 密钥未配置时抛出
     */
    @Bean
    @Primary
    public ChatClient defaultChatClient() {
        AiProperties.ModelProperties modelProperties = getModelProperties(aiProperties.getDefaultModel());

        String baseUrl = modelProperties.getBaseUrl();
        String apiKey = modelProperties.getApiKey();
        String model = modelProperties.getModel();

        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            String errorMessage = localeContextService.getMessageWithArgs(
                "error.base-url-missing",
                aiProperties.getDefaultModel()
            );
            throw new IllegalStateException(errorMessage);
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            String errorMessage = localeContextService.getMessageWithArgs(
                "error.api-key-missing",
                aiProperties.getDefaultModel()
            );
            throw new IllegalStateException(errorMessage);
        }

        OpenAiApi api = new OpenAiApi(baseUrl, apiKey);
        return new OpenAiChatClient(api, OpenAiChatOptions.builder().withModel(model).build());
    }

    /**
     * 创建 DeepSeek ChatClient Bean
     * <p>
     * 使用 {@code @Qualifier("deepseekChatClient")} 注解标记，
     * 可通过 {@code @Qualifier} 注解注入使用。
     * </p>
     * <p>
     * 该 Bean 为条件创建，需要配置 {@code spring.ai.deepseek.enabled=true} 才会生效。
     * </p>
     *
     * @return DeepSeek 的 ChatClient 实例
     * @throws IllegalStateException 当 DeepSeek 的 API 配置不完整时抛出
     */
    @Bean
    @Qualifier("deepseekChatClient")
    @ConditionalOnProperty(prefix = "spring.ai.deepseek", name = "enabled", havingValue = "true", matchIfMissing = false)
    public ChatClient deepseekChatClient() {
        AiProperties.ModelProperties properties = aiProperties.getDeepseek();
        validateModelProperties(properties, "deepseek");

        OpenAiApi api = new OpenAiApi(properties.getBaseUrl(), properties.getApiKey());
        return new OpenAiChatClient(api, OpenAiChatOptions.builder().withModel(properties.getModel()).build());
    }

    /**
     * 创建 Qwen (通义千问) ChatClient Bean
     * <p>
     * 使用 {@code @Qualifier("qwenChatClient")} 注解标记，
     * 可通过 {@code @Qualifier} 注解注入使用。
     * </p>
     * <p>
     * 支持的模型包括：qwen-turbo、qwen-plus、qwen-max 等。
     * </p>
     * <p>
     * 该 Bean 为条件创建，需要配置 {@code spring.ai.qwen.enabled=true} 才会生效。
     * </p>
     *
     * @return Qwen 的 ChatClient 实例
     * @throws IllegalStateException 当 Qwen 的 API 配置不完整时抛出
     */
    @Bean
    @Qualifier("qwenChatClient")
    @ConditionalOnProperty(prefix = "spring.ai.qwen", name = "enabled", havingValue = "true", matchIfMissing = false)
    public ChatClient qwenChatClient() {
        AiProperties.ModelProperties properties = aiProperties.getQwen();
        validateModelProperties(properties, "qwen");

        OpenAiApi api = new OpenAiApi(properties.getBaseUrl(), properties.getApiKey());
        return new OpenAiChatClient(api, OpenAiChatOptions.builder().withModel(properties.getModel()).build());
    }

    /**
     * 创建 Qwen3 Coder Next ChatClient Bean
     * <p>
     * 使用 {@code @Qualifier("qwen3CoderNextChatClient")} 注解标记，
     * 可通过 {@code @Qualifier} 注解注入使用。
     * </p>
     * <p>
     * 通义千问最新代码专家模型，适用于代码生成和理解任务。
     * </p>
     * <p>
     * 该 Bean 为条件创建，需要配置 {@code spring.ai.qwen3-coder-next.enabled=true} 才会生效。
     * </p>
     *
     * @return Qwen3 Coder Next 的 ChatClient 实例
     * @throws IllegalStateException 当 Qwen3 Coder Next 的 API 配置不完整时抛出
     */
    @Bean
    @Qualifier("qwen3CoderNextChatClient")
    @ConditionalOnProperty(prefix = "spring.ai.qwen3-coder-next", name = "enabled", havingValue = "true", matchIfMissing = false)
    public ChatClient qwen3CoderNextChatClient() {
        AiProperties.ModelProperties properties = aiProperties.getQwen3CoderNext();
        validateModelProperties(properties, "qwen3-coder-next");

        OpenAiApi api = new OpenAiApi(properties.getBaseUrl(), properties.getApiKey());
        return new OpenAiChatClient(api, OpenAiChatOptions.builder().withModel(properties.getModel()).build());
    }

    /**
     * 根据模型名称获取对应的配置属性
     * <p>
     * 支持的模型名称：deepseek, qwen, qwen3-coder-next
     * </p>
     *
     * @param modelName 模型名称
     * @return 对应的模型配置属性对象
     */
    private AiProperties.ModelProperties getModelProperties(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return aiProperties.getDeepseek();
        }

        switch (modelName.toLowerCase()) {
            case "qwen":
                return aiProperties.getQwen();
            case "qwen3-coder-next":
                return aiProperties.getQwen3CoderNext();
            case "deepseek":
            default:
                return aiProperties.getDeepseek();
        }
    }

    /**
     * 验证模型配置是否完整
     * <p>
     * 检查 API 基础 URL 和 API 密钥是否已配置。
     * </p>
     *
     * @param properties  模型配置属性对象
     * @param modelName 模型名称（用于错误提示）
     * @throws IllegalStateException 当配置不完整时抛出
     */
    private void validateModelProperties(AiProperties.ModelProperties properties, String modelName) {
        if (properties.getBaseUrl() == null || properties.getBaseUrl().trim().isEmpty()) {
            String errorMessage = localeContextService.getMessageWithArgs(
                "error.base-url-missing",
                modelName
            );
            throw new IllegalStateException(errorMessage);
        }
        if (properties.getApiKey() == null || properties.getApiKey().trim().isEmpty()) {
            String errorMessage = localeContextService.getMessageWithArgs(
                "error.api-key-missing",
                modelName
            );
            throw new IllegalStateException(errorMessage);
        }
    }
}
