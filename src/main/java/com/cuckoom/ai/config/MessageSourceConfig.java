package com.cuckoom.ai.config;

import com.cuckoom.ai.context.LocaleContextService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

/**
 * 消息资源配置类
 * <p>
 * 配置 Spring 的 MessageSource 和自定义的语言上下文服务。
 * 资源文件位于 classpath 下，基础名为 messages。
 * </p>
 *
 * @author Cuckoom
 */
@Configuration
public class MessageSourceConfig {

    /**
     * 创建 MessageSource Bean
     * <p>
     * 配置资源文件的基础名称、编码。
     * </p>
     *
     * @return 配置好的 MessageSource 实例
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

    /**
     * 创建 LocaleContextService Bean
     * <p>
     * 封装了 MessageSource 和 AppProperties，提供统一的语言服务。
     * 此 Bean 可以注入到任何需要多语言支持的类中。
     * </p>
     *
     * @param messageSource 消息资源
     * @param appProperties 应用配置属性
     * @return LocaleContextService 实例
     */
    @Bean
    public LocaleContextService localeContextService(MessageSource messageSource, AppProperties appProperties) {
        return new LocaleContextService(messageSource, appProperties);
    }
}
