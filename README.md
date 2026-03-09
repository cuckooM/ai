# Spring AI DeepSeek 对接框架

基于 Spring AI 的 DeepSeek 模型对接框架。

## 功能特性

- ✅ 支持 DeepSeek 模型
- ✅ 统一的 REST API 接口
- ✅ 命令行交互演示
- ✅ 可扩展的架构设计（未来可添加更多模型）

## 快速开始

### 1. 环境要求

- Java 21+
- Gradle 9.3+
- 有效的 API 密钥（至少一个模型）

### 2. 配置 API 密钥

在运行前，设置环境变量：

```bash
# DeepSeek
export DEEPSEEK_API_KEY=your_deepseek_key
```

或者直接修改 `src/main/resources/application.yaml` 文件。

### 3. 运行应用

```bash
# 编译并运行
./gradlew bootRun

# 或单独编译后运行
./gradlew build
java -jar build/libs/ai-0.0.1-SNAPSHOT.jar
```

### 4. 使用方式

#### 命令行交互

应用启动后，会自动进入命令行交互模式：

```
=== Spring AI 对话演示 ===
使用 DeepSeek 模型进行对话
输入 'exit' 退出程序
请输入你的消息：
> 你好，介绍一下自己
AI: 我是阿里云的通义千问，一个大型语言模型...
```

#### REST API 接口

**聊天接口** `POST /api/chat`

请求示例：
```json
{
  "message": "你好，请介绍一下自己",
  "model": "deepseek"  // 可选：deepseek
}
```

响应示例：
```json
{
  "reply": "我是阿里云的通义千问..."
}
```

使用 curl 测试：
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好", "model": "deepseek"}'
```

## 项目结构

```
src/main/java/com/cuckoom/ai/
├── AiApplication.java              # 主应用入口
├── config/
│   └── MultiModelAiConfig.java     # 多模型配置
├── controller/
│   └── ChatController.java         # REST API 控制器
└── runner/
    └── DemoRunner.java             # 命令行演示
```

## 模型配置

### 默认配置

| 模型 | API端点 | 默认模型名称 |
|------|---------|-------------|
| OpenAI | https://api.openai.com | gpt-3.5-turbo |
| 千问 | https://dashscope.aliyuncs.com/compatible-mode/v1 | qwen-turbo |
| DeepSeek | https://api.deepseek.com | deepseek-chat |

### 自定义配置

修改 `application.yaml`：

```yaml
spring:
  ai:
    openai:
      base-url: https://api.openai.com
      api-key: ${OPENAI_API_KEY:}
      model: gpt-4  # 可更改为其他模型
    qianwen:
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      api-key: ${QIANWEN_API_KEY:}
      model: qwen-plus  # 可更改为其他千问模型
    deepseek:
      base-url: https://api.deepseek.com
      api-key: ${DEEPSEEK_API_KEY:}
      model: deepseek-coder  # 可更改为其他DeepSeek模型

# 默认使用的模型
ai.default-model: qianwen
```

## 扩展新模型

要添加新的 AI 模型支持：

1. 在 `application.yaml` 中添加配置：
   ```yaml
   spring.ai.newmodel:
     base-url: https://api.newmodel.com
     api-key: ${NEWMODEL_API_KEY:}
     model: model-name
   ```

2. 在 `MultiModelAiConfig.java` 中添加 Bean：
   ```java
   @Bean
   @Qualifier("newmodelChatClient")
   public ChatClient newmodelChatClient(...) {
       // 配置新的ChatClient
   }
   ```

3. 在模型映射表中注册：
   ```java
   map.put("newmodel", newmodelChatClient);
   ```

## 测试

```bash
# 运行所有测试
./gradlew test

# 运行特定测试
./gradlew test --tests "*ChatControllerTest*"
```

## 故障排除

### 1. API 密钥错误

确保正确设置环境变量或直接在配置文件中配置。

### 2. 网络连接问题

检查网络连接，确保可以访问对应的 API 端点。

### 3. 模型不支持错误

检查请求中的模型名称是否正确，支持：`openai`、`qianwen`、`deepseek`。

### 4. 编译错误

确保使用正确的 Java 版本（21+）和 Gradle 版本（9.3+）。

## 技术栈

- **Spring Boot 3.2.5** - 应用框架
- **Spring AI 0.8.1** - AI 集成框架
- **Spring Web** - REST API 支持
- **OpenAI Java SDK** - OpenAI API 客户端
- **Gradle** - 构建工具

## 许可证

MIT License