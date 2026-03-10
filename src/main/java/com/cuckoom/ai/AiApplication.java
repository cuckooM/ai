package com.cuckoom.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI 多模型对话应用主入口
 * <p>
 * 该应用基于 Spring AI 框架，支持对接多个 AI 模型服务商，
 * 包括 DeepSeek、通义千问（Qwen）和 Qwen3 Coder Next。
 * </p>
 * <p>
 * 功能特性：
 * <ul>
 *   <li>命令行交互式对话演示</li>
 *   <li>REST API 聊天接口</li>
 *   <li>支持运行时模型切换</li>
 *   <li>统一的多模型配置管理</li>
 * </ul>
 * </p>
 *
 * @author Cuckoom
 */
@SpringBootApplication
public class AiApplication {

    /**
     * 应用主入口
     * <p>
     * 启动 Spring Boot 应用，初始化所有 Bean 和配置。
     * </p>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
    }

}
