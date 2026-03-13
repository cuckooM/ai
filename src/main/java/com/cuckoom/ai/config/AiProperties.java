package com.cuckoom.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 模型配置属性类
 * <p>
 * 通过 {@code @ConfigurationProperties(prefix = "spring.ai")} 注解，
 * 自动绑定 {@code application.yaml} 中的 {@code spring.ai.*} 配置属性。
 * </p>
 * <p>
 * 支持的配置项：
 * <ul>
 *   <li>defaultModel: 默认使用的模型名称</li>
 *   <li>deepseek: DeepSeek 模型配置</li>
 *   <li>qwen: 通义千问模型配置</li>
 *   <li>qwen3-coder-next: 通义千问3 代码专家模型配置</li>
 * </ul>
 * </p>
 *
 * @author Cuckoom
 */
@Component
@ConfigurationProperties(prefix = "spring.ai")
@Data
public class AiProperties {

    /**
     * 默认使用的模型名称
     * <p>
     * 可选值：deepseek, qwen, qwen3-coder-next
     * 默认值：deepseek
     * </p>
     * <p>
     * 通过以下方式配置：
     * <pre>{@code
     * spring.ai.default-model: qwen
     * }</pre>
     * </p>
     */
    private String defaultModel = "deepseek";

    /**
     * DeepSeek 模型配置属性
     */
    private ModelProperties deepseek = new ModelProperties();

    /**
     * Qwen (通义千问) 模型配置属性
     */
    private ModelProperties qwen = new ModelProperties();

    /**
     * Qwen3 Coder Next 模型配置属性
     */
    private ModelProperties qwen3CoderNext = new ModelProperties();

    /**
     * 模型配置属性内部类
     * <p>
     * 封装模型的基本配置信息，包括 API 基础 URL、API 密钥和模型名称。
     * </p>
     */
    @Data
    public static class ModelProperties {
        /**
         * API 基础 URL
         * <p>
         * 例如：https://api.deepseek.com
         *       https://dashscope.aliyuncs.com/compatible-mode/v1
         * </p>
         */
        private String baseUrl;

        /**
         * API 密钥
         * <p>
         * 用于身份认证的 API Key，需从对应服务商获取。
         * </p>
         */
        private String apiKey;

        /**
         * 模型名称
         * <p>
         * DeepSeek 默认：deepseek-chat
         * Qwen 默认：qwen-turbo
         * Qwen3 Coder Next 默认：qwen3-coder-next
         * </p>
         */
        private String model = "default-model";
    }
}
