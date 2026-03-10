package com.cuckoom.ai.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天响应实体类
 * <p>
 * 封装 API 响应中的 AI 回复内容。
 * </p>
 *
 * @author Cuckoom
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatApiResponse {
    /**
     * AI 回复内容
     */
    private String reply;
}
