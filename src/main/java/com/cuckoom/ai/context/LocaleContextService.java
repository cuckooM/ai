package com.cuckoom.ai.context;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import com.cuckoom.ai.config.AppProperties;

import java.util.Locale;

/**
 * 语言上下文服务
 * <p>
 * 封装了 MessageSource 和 LocaleContext，提供统一的语言相关服务。
 * 支持全局语言切换，所有使用该服务的地方都会自动使用新的语言设置。
 * </p>
 *
 * @author Cuckoom
 */
@RequiredArgsConstructor
public class LocaleContextService {

    private final MessageSource messageSource;
    private final AppProperties appProperties;

    /**
     * 获取当前语言环境
     *
     * @return 当前语言环境
     */
    public Locale getLocale() {
        String languageCode = appProperties.getLanguage();
        return parseLocale(languageCode);
    }

    /**
     * 根据语言代码获取语言环境
     *
     * @param languageCode 语言代码
     * @return 对应的语言环境
     */
    public Locale getLocale(String languageCode) {
        return parseLocale(languageCode);
    }

    /**
     * 获取消息（使用当前语言环境）
     *
     * @param key 消息键
     * @return 消息内容
     */
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, getLocale());
    }

    /**
     * 获取消息（使用当前语言环境）
     *
     * @param key 消息键
     * @param args 消息参数
     * @return 格式化后的消息内容
     */
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, getLocale());
    }

    /**
     * 获取消息（使用当前语言环境），支持单个参数
     *
     * @param key 消息键
     * @param arg 消息参数
     * @return 格式化后的消息内容
     */
    public String getMessageWithArgs(String key, Object arg) {
        return messageSource.getMessage(key, new Object[]{arg}, getLocale());
    }

    /**
     * 获取消息（使用当前语言环境），支持多个参数
     *
     * @param key 消息键
     * @param args 消息参数
     * @return 格式化后的消息内容
     */
    public String getMessageWithArgs(String key, String... args) {
        return messageSource.getMessage(key, args, getLocale());
    }

    /**
     * 获取消息（指定语言环境）
     *
     * @param key 消息键
     * @param locale 语言环境
     * @return 消息内容
     */
    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    /**
     * 获取消息（指定语言环境）
     *
     * @param key 消息键
     * @param locale 语言环境
     * @param args 消息参数
     * @return 格式化后的消息内容
     */
    public String getMessage(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }

    /**
     * 切换语言
     *
     * @param languageCode 语言代码（zh, en, ja, fr, zh_tw）
     */
    public void setLanguage(String languageCode) {
        if (languageCode != null && !languageCode.trim().isEmpty()) {
            appProperties.setLanguage(languageCode.toLowerCase());
        }
    }

    /**
     * 解析语言代码为 Locale
     *
     * @param languageCode 语言代码
     * @return Locale 对象
     */
    private Locale parseLocale(String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            return Locale.getDefault();
        }

        String lowerCode = languageCode.toLowerCase();
        switch (lowerCode) {
            case "zh":
                return new Locale("zh", "CN");
            case "en":
                return Locale.ENGLISH;
            case "ja":
                return Locale.JAPANESE;
            case "fr":
                return Locale.FRENCH;
            case "zh_tw":
            case "zh-tw":
                return new Locale("zh", "TW");
            default:
                return new Locale("zh", "CN");
        }
    }
}
