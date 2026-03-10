package com.cuckoom.ai.runner;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.cuckoom.ai.context.LocaleContextService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import jakarta.annotation.PostConstruct;

/**
 * 演示运行器
 * <p>
 * 提供命令行交互式对话功能，支持多模型选择和切换。
 * 该运行器在应用启动后自动执行，适用于开发测试和演示场景。
 * </p>
 * <p>
 * 使用方式：
 * <ul>
 *   <li>直接运行：使用默认模型进行对话</li>
 *   <li>指定模型运行：java -jar app.jar --model=qwen</li>
 *   <li>非交互式环境：建议使用 REST API 而非命令行交互</li>
 * </ul>
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
 * 支持的命令：
 * <ul>
 *   <li>/help 或 /? - 显示帮助信息</li>
 *   <li>/list - 显示支持的模型列表</li>
 *   <li>model &lt;name&gt; - 切换模型（例如: model qwen）</li>
 *   <li>lang &lt;code&gt; - 切换语言（例如: lang en）</li>
 *   <li>exit - 退出程序</li>
 * </ul>
 * </p>
 * <p>
 * 语言代码支持：
 * <ul>
 *   <li>zh - 简体中文</li>
 *   <li>en - 英语</li>
 *   <li>ja - 日语</li>
 *   <li>fr - 法语</li>
 *   <li>zh_tw - 繁体中文</li>
 * </ul>
 * </p>
 * <p>
 * 注意：此运行器需要交互式终端才能正常工作。
 * 在非交互式环境中（如 CI/CD），请禁用此功能或使用 REST API。
 * </p>
 *
 * @author Cuckoom
 */
@Component
@Profile("!test")
public class DemoRunner implements CommandLineRunner {

    /**
     * 默认 ChatClient 实例
     * <p>
     * 用于与默认模型进行对话。
     * </p>
     */
    @Autowired
    private ChatClient defaultChatClient;

    /**
     * 模型与 ChatClient 的映射表
     * <p>
     * 存储所有可用的模型客户端实例，支持运行时切换。
     * </p>
     */
    @Autowired(required = false)
    private Map<String, ChatClient> chatClients;

    /**
     * 默认模型名称
     * <p>
     * 从系统属性或环境变量中读取，用于确定启动时使用的模型。
     * 读取顺序：系统属性 model > 环境变量 AI_DEFAULT_MODEL > 默认模型
     * </p>
     */
    private String defaultModel;

    /**
     * 语言上下文服务
     * <p>
     * 统一的语言服务，封装了 MessageSource 和语言环境管理。
     * </p>
     */
    @Autowired
    private LocaleContextService localeContextService;

    /**
     * 初始化 chatClients Map（处理 @Autowired(required = false) 的情况）
     */
    @PostConstruct
    public void init() {
        if (chatClients == null) {
            chatClients = new HashMap<>();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // 解析命令行参数指定的模型
        String selectedModel = parseModelFromArgs(args);
        if (selectedModel == null) {
            selectedModel = defaultModel;
        }

        // 从系统属性或环境变量中读取语言代码
        String languageCode = System.getProperty("language", System.getenv("APP_LANGUAGE"));
        if (languageCode != null && !languageCode.trim().isEmpty()) {
            localeContextService.setLanguage(languageCode);
        }

        // 检查是否有可用的 ChatClient
        if (defaultChatClient == null) {
            String errorMsg = localeContextService.getMessage("demorunner.error.no-chatclient-found");
            System.err.println(errorMsg);
            return;
        }

        ChatClient chatClient = getChatClient(selectedModel);

        // 打印欢迎信息
        printWelcomeInfo(selectedModel);

        // 检查是否在交互式终端中运行
        if (System.console() == null) {
            String info = localeContextService.getMessage("demorunner.info.non-interactive-environment");
            System.out.println(info);
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String userInput = readInput(scanner);
                if (userInput == null) {
                    break; // EOF 或输入流关闭
                }

                String trimmedInput = userInput.trim();
                if ("exit".equalsIgnoreCase(trimmedInput)) {
                    String exitMsg = localeContextService.getMessage("demorunner.info.exit");
                    System.out.println(exitMsg);
                    break;
                }
                if (trimmedInput.isEmpty()) {
                    continue;
                }

                // 处理语言切换命令
                if (trimmedInput.toLowerCase().startsWith("lang ")) {
                    String newLanguage = trimmedInput.substring(5).trim();
                    switchLanguage(newLanguage);
                    continue;
                }

                // 处理模型切换命令
                if (trimmedInput.toLowerCase().startsWith("model ")) {
                    String newModel = trimmedInput.substring(6).trim();
                    ChatClient newClient = getChatClient(newModel);
                    if (newClient != null) {
                        chatClient = newClient;
                        selectedModel = newModel;
                        String switchedMsg = localeContextService.getMessageWithArgs("demorunner.info.model-switched", getDisplayName(newModel));
                        System.out.println(switchedMsg);
                    } else {
                        String errorMsg = localeContextService.getMessageWithArgs("demorunner.error.unsupported-model", newModel);
                        System.err.println(errorMsg);
                    }
                    continue;
                }

                // 处理特殊命令
                if (trimmedInput.startsWith("/")) {
                    handleCommand(trimmedInput, chatClient);
                    continue;
                }

                try {
                    ChatResponse response = chatClient.call(new Prompt(trimmedInput));
                    String aiResponse = response.getResult().getOutput().getContent();
                    System.out.println("AI: " + aiResponse);
                } catch (Exception e) {
                    String errorMsg = localeContextService.getMessageWithArgs("error.runtime-error", e.getMessage());
                    System.err.println(errorMsg);
                    // 不打印堆栈跟踪，避免泄露敏感信息
                }
            }
        } catch (Exception e) {
            String errorMsg = localeContextService.getMessageWithArgs("error.runtime-error", e.getMessage());
            System.err.println(errorMsg);
        }
    }

    /**
     * 切换语言
     *
     * @param languageCode 语言代码
     */
    private void switchLanguage(String languageCode) {
        localeContextService.setLanguage(languageCode);
        String currentLang = getLocaleLabel();
        String msg = localeContextService.getMessageWithArgs("demorunner.info.language-switched", currentLang);
        System.out.println(msg);
    }

    /**
     * 获取当前语言标签
     *
     * @return 当前语言标签
     */
    private String getLocaleLabel() {
        var locale = localeContextService.getLocale();
        String language = locale.getLanguage();
        String country = locale.getCountry();

        if ("zh".equals(language)) {
            if ("CN".equals(country)) {
                return "简体中文";
            } else if ("TW".equals(country)) {
                return "繁体中文";
            }
            return "中文";
        } else if ("en".equals(language)) {
            return "English";
        } else if ("ja".equals(language)) {
            return "日本語";
        } else if ("fr".equals(language)) {
            return "Français";
        }
        return locale.toString();
    }

    /**
     * 根据键和参数获取消息
     *
     * @param key 消息键
     * @param args 消息参数
     * @return 格式化后的消息内容
     */
    private String getMessageWithArgs(String key, Object... args) {
        return localeContextService.getMessage(key, args);
    }

    /**
     * 打印欢迎信息
     *
     * @param modelName 模型名称
     */
    private void printWelcomeInfo(String modelName) {
        String welcomeTitle = localeContextService.getMessage("cli.welcome-title");
        System.out.println("\n" + welcomeTitle);

        String modelLabel = getDisplayName(modelName);
        String welcomeModel = localeContextService.getMessage("cli.welcome-model");
        System.out.println(welcomeModel + " " + modelLabel);

        String welcomeExit = localeContextService.getMessage("cli.welcome-exit-command");
        System.out.println(welcomeExit);

        String welcomeList = localeContextService.getMessage("cli.welcome-list-command");
        System.out.println(welcomeList);

        String welcomeSwitch = localeContextService.getMessage("cli.welcome-switch-command");
        System.out.println(welcomeSwitch);

        String langSwitch = localeContextService.getMessage("cli.welcome-lang-command");
        System.out.println(langSwitch);

        String inputPrompt = localeContextService.getMessage("cli.input-prompt");
        System.out.println(inputPrompt);
        System.out.println();
    }

    /**
     * 读取用户输入
     * <p>
     * 安全地从 Scanner 读取一行输入，处理可能的异常。
     * </p>
     *
     * @param scanner Scanner 实例
     * @return 用户输入的行，如果读取失败或到达 EOF 则返回 null
     */
    private String readInput(Scanner scanner) {
        try {
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            }
            return null;
        } catch (Exception e) {
            String errorMsg = localeContextService.getMessageWithArgs("error.read-input-error", e.getMessage());
            System.err.println(errorMsg);
            return null;
        }
    }

    /**
     * 从命令行参数中解析模型名称
     * <p>
     * 支持格式：--model=&lt;model-name&gt;
     * </p>
     *
     * @param args 命令行参数数组
     * @return 解析出的模型名称，如果未找到则返回 null
     */
    private String parseModelFromArgs(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--model=")) {
                String value = arg.substring("--model=".length());
                return value.isEmpty() ? null : value;
            }
        }
        return null;
    }

    /**
     * 获取模型的显示名称
     * <p>
     * 将模型名称转换为用户友好的显示名称。
     * </p>
     *
     * @param modelName 模型名称
     * @return 模型的显示名称
     */
    private String getDisplayName(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return localeContextService.getMessage("model.default");
        }
        String key = "model." + modelName.toLowerCase();
        String displayName = localeContextService.getMessage(key);
        return !key.equals(displayName) ? displayName : modelName;
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
            String warningMsg = localeContextService.getMessageWithArgs("demorunner.warning.model-not-found", modelName);
            System.err.println(warningMsg);
            return defaultChatClient;
        }
        return client;
    }

    /**
     * 处理特殊命令
     * <p>
     * 支持的命令：/help, /?, /list
     * </p>
     *
     * @param command 用户输入的命令
     * @param chatClient 当前 ChatClient 实例
     */
    private void handleCommand(String command, ChatClient chatClient) {
        switch (command.toLowerCase()) {
            case "/help":
            case "/?":
                printHelp();
                break;
            case "/list":
                printModels();
                break;
            default:
                String errorMsg = localeContextService.getMessageWithArgs("demorunner.error.unsupported-model", command);
                System.err.println(errorMsg);
        }
    }

    /**
     * 打印帮助信息
     * <p>
     * 显示支持的命令列表和使用说明。
     * </p>
     */
    private void printHelp() {
        String header = localeContextService.getMessage("command.help-header");
        System.out.println("\n" + header);

        String helpCmd = localeContextService.getMessage("command.help.help");
        String listCmd = localeContextService.getMessage("command.help.list");
        String modelCmd = localeContextService.getMessage("command.help.model");
        String langCmd = localeContextService.getMessage("command.help.lang");
        String exitCmd = localeContextService.getMessage("command.help.exit");

        System.out.println("  /help    - " + helpCmd);
        System.out.println("  /list    - " + listCmd);
        System.out.println("  model <name> - " + modelCmd);
        System.out.println("  lang <code>  - " + langCmd);
        System.out.println("  exit     - " + exitCmd);
        System.out.println();
    }

    /**
     * 打印支持的模型列表
     * <p>
     * 显示所有可用的 AI 模型及其描述。
     * </p>
     */
    private void printModels() {
        String header = localeContextService.getMessage("command.list-header");
        System.out.println("\n" + header);
        System.out.println("  deepseek     - " + getDisplayName("deepseek"));
        System.out.println("  qwen         - " + getDisplayName("qwen"));
        System.out.println("  qwen3-coder-next - " + getDisplayName("qwen3-coder-next"));
        System.out.println();
    }
}
