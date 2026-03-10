package com.cuckoom.ai.controller;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cuckoom.ai.context.LocaleContextService;

import java.util.HashMap;
import java.util.Map;

/**
 * 聊天控制器
 * <p>
 * 提供 REST API 接口，支持多 AI 模型的对话服务。
 * 可通过 model 参数选择使用的模型，或使用默认模型。
 * </p>
 * <p>
 * 支持的模型：
 * <ul>
 *   <li>deepseek: DeepSeek 模型</li>
 *   <li>qwen: 通义千问 qwen-turbo 模型</li>
 *   <li>qwen3-coder-next: 通义千问3 代码专家模型</li>
 * </ul>
 * </p>
 * <p>
 * 使用方式：
 * <pre>{@code
 * # 聊天接口（POST）
 * POST /api/chat
 * Content-Type: application/json
 *
 * {
 *   "message": "你好，请介绍一下自己",
 *   "model": "deepseek"
 * }
 *
 * # 响应
 * {
 *   "reply": "我是..."
 * }
 * }</pre>
 * </p>
 *
 * @author Cuckoom
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    /**
     * 默认 ChatClient 实例
     * <p>
     * 当未指定模型或指定的模型不可用时，使用此默认客户端。
     * </p>
     */
    @Autowired
    private ChatClient defaultChatClient;

    /**
     * 语言上下文服务
     * <p>
     * 统一的语言服务，用于获取多语言错误消息。
     * </p>
     */
    @Autowired
    private LocaleContextService localeContextService;

    /**
     * 模型与 ChatClient 的映射表
     * <p>
     * 存储所有可用模型的客户端实例。
     * </p>
     */
    private Map<String, ChatClient> chatClients;

    /**
     * 构造函数注入所有可用的 ChatClient
     *
     * @param defaultChatClient 默认 ChatClient（必需）
     * @param deepseekChatClient DeepSeek ChatClient（可选）
     * @param qwenChatClient Qwen ChatClient（可选）
     * @param qwen3CoderNextChatClient Qwen3 Coder Next ChatClient（可选）
     */
    @Autowired
    public ChatController(
            ChatClient defaultChatClient,
            @Autowired(required = false) @Qualifier("deepseekChatClient") ChatClient deepseekChatClient,
            @Autowired(required = false) @Qualifier("qwenChatClient") ChatClient qwenChatClient,
            @Autowired(required = false) @Qualifier("qwen3CoderNextChatClient") ChatClient qwen3CoderNextChatClient) {
        this.defaultChatClient = defaultChatClient;
        this.chatClients = new HashMap<>();
        if (deepseekChatClient != null) {
            chatClients.put("deepseek", deepseekChatClient);
        }
        if (qwenChatClient != null) {
            chatClients.put("qwen", qwenChatClient);
        }
        if (qwen3CoderNextChatClient != null) {
            chatClients.put("qwen3-coder-next", qwen3CoderNextChatClient);
        }
    }

    /**
     * 聊天接口（JSON 请求体）
     * <p>
     * 通过 POST 请求发送消息，支持指定模型名称。
     * </p>
     *
     * @param request 聊天请求对象，包含 message 和可选的 model 字段
     * @return 聊天响应对象，包含 AI 回复内容
     */
    @PostMapping
    public ChatApiResponse chat(@RequestBody ChatApiRequest request) {
        validateRequest(request);
        String modelName = request.getModel();
        ChatClient chatClient = getChatClient(modelName);
        ChatResponse response = chatClient.call(new Prompt(request.getMessage()));
        return new ChatApiResponse(response.getResult().getOutput().getContent());
    }

    /**
     * 聊天接口（URL 参数方式）
     * <p>
     * 通过 URL 参数传递消息和可选的模型名称。
     * </p>
     * <p>
     * 示例：POST /api/chat/param?message=你好&model=qwen
     * </p>
     *
     * @param message 消息内容（必需）
     * @param model 模型名称（可选）
     * @return 聊天响应对象，包含 AI 回复内容
     */
    @PostMapping("/param")
    public ChatApiResponse chatByParam(
            @RequestParam String message,
            @RequestParam(required = false) String model) {
        ChatClient chatClient = getChatClient(model);
        ChatResponse response = chatClient.call(new Prompt(message));
        return new ChatApiResponse(response.getResult().getOutput().getContent());
    }

    /**
     * 验证聊天请求
     * <p>
     * 检查消息内容是否为空。
     * </p>
     *
     * @param request 聊天请求对象
     * @throws IllegalArgumentException 当消息内容为空时抛出
     */
    private void validateRequest(ChatApiRequest request) {
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            String errorMsg = localeContextService.getMessage("error.chat-api.request-message-required");
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * 获取指定模型的 ChatClient
     * <p>
     * 如果指定的模型不存在或为 null，则返回默认客户端。
     * </p>
     *
     * @param modelName 模型名称
     * @return 对应的 ChatClient 实例，如果不存在则返回默认客户端
     */
    private ChatClient getChatClient(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return defaultChatClient;
        }

        ChatClient client = chatClients.get(modelName.toLowerCase());
        if (client == null) {
            // 如果模型不可用，使用默认客户端
            return defaultChatClient;
        }
        return client;
    }
}
