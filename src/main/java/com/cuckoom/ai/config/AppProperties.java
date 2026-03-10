package com.cuckoom.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置属性
 * <p>
 * 包含应用级别的配置，如默认语言等。
 * </p>
 *
 * @author Cuckoom
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * 默认语言
     * <p>
     * 支持的值：zh（中文）、en（英文）、ja（日文）、fr（法文）、zh_TW（繁体中文）
     * 默认值：zh
     * </p>
     */
    private String language = "zh";
}
