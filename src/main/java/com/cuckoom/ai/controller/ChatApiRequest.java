package com.cuckoom.ai.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天请求实体类
 * <p>
 * 封装 API 请求中的消息和模型参数。
 * </p>
 *
 * @author Cuckoom
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatApiRequest {
    /**
     * 消息内容
     */
    private String message;
    /**
     * 模型名称（可选）
     */
    private String model;
}
