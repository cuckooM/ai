package com.cuckoom.ai.runner;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * 演示运行器，支持控制台对话（使用 Spring AI 自动配置的 ChatClient）
 */
@Component
@Profile("!test")
public class DemoRunner implements CommandLineRunner {

    private final ChatClient chatClient;

    @Autowired
    public DemoRunner(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Spring AI 对话演示 ===");
        System.out.println("使用 DeepSeek 模型进行对话");
        System.out.println("输入 'exit' 退出程序");
        System.out.println("请输入你的消息：");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String userInput = scanner.nextLine().trim();
            if ("exit".equalsIgnoreCase(userInput)) {
                System.out.println("再见！");
                break;
            }
            if (userInput.isEmpty()) {
                continue;
            }
            try {
                ChatResponse response = chatClient.call(new Prompt(userInput));
                String aiResponse = response.getResult().getOutput().getContent();
                System.out.println("AI: " + aiResponse);
            } catch (Exception e) {
                System.err.println("错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }

}