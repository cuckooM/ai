# Spring AI 多模型对接框架

基于 Spring AI 的多模型对接框架，支持 DeepSeek、通义千问（Qwen）和 Qwen3 Coder Next 模型。

## 功能特性

- ✅ 支持 DeepSeek 模型
- ✅ 支持通义千问 qwen-turbo 模型
- ✅ 支持通义千问 qwen3-coder-next 模型
- ✅ 统一的 REST API 接口
- ✅ 命令行交互演示
- ✅ 支持运行时模型切换

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

# 通义千问（可选）
export QWEN_API_KEY=your_qwen_key
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
使用的模型: DeepSeek
输入 'exit' 退出程序
输入 'list' 查看支持的模型
输入 'model <name>' 切换模型
请输入你的消息：

> 你好，介绍一下自己
AI: 我是阿里云的通义千问，一个大型语言模型...
```

**支持的命令：**
- `/help` - 显示帮助信息
- `/list` - 显示支持的模型列表
- `model <name>` - 切换模型（例如: `model qwen`）
- `exit` - 退出程序

#### REST API 接口

**聊天接口** `POST /api/chat`

请求示例：
```json
{
  "message": "你好，请介绍一下自己",
  "model": "deepseek"
}
```

响应示例：
```json
{
  "reply": "这是一个测试回复示例..."
}
```

通过 `model` 参数指定使用的模型（可选）：
- `deepseek` - DeepSeek 模型
- `qwen` - 通义千问 qwen-turbo 模型
- `qwen3-coder-next` - 通义千问3 代码专家模型

如果未指定 `model` 参数，将使用配置文件中默认的模型。

使用 curl 测试：
```bash
# 使用默认模型
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'

# 使用 DeepSeek 模型
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好", "model": "deepseek"}'

# 使用 Qwen 模型
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好", "model": "qwen"}'

# 使用 Qwen3 Coder Next 模型
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好", "model": "qwen3-coder-next"}'
```

## 模型配置

### 默认配置

| 模型 | API端点 | 默认模型名称 | 配置前缀 |
|------|---------|-------------|----------|
| DeepSeek | https://api.deepseek.com | deepseek-chat | `spring.ai.deepseek` |
| Qwen | https://dashscope.aliyuncs.com/compatible-mode/v1 | qwen-turbo | `spring.ai.qwen` |
| Qwen3 Coder Next | https://dashscope.aliyuncs.com/compatible-mode/v1 | qwen3-coder-next | `spring.ai.qwen3-coder-next` |

### 自定义配置

修改 `src/main/resources/application.yaml`：

```yaml
spring:
  ai:
    # 默认使用的模型
    default-model: qwen

    # DeepSeek 配置
    deepseek:
      enabled: true
      base-url: https://api.deepseek.com
      api-key: ${DEEPSEEK_API_KEY:your_key}
      model: deepseek-chat

    # Qwen 配置
    qwen:
      enabled: true
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      api-key: ${QWEN_API_KEY:your_key}
      model: qwen-plus

    # Qwen3 Coder Next 配置
    qwen3-coder-next:
      enabled: true
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      api-key: ${QWEN_API_KEY:your_key}
      model: qwen3-coder-next
```

### 环境变量支持

| 环境变量 | 说明 |
|---------|------|
| `AI_DEFAULT_MODEL` | 默认使用的模型（deepseek, qwen, qwen3-coder-next） |
| `DEEPSEEK_API_KEY` | DeepSeek API 密钥 |
| `DEEPSEEK_BASE_URL` | DeepSeek API 基础 URL |
| `DEEPSEEK_MODEL` | DeepSeek 模型名称 |
| `QWEN_API_KEY` | 通义千问 API 密钥 |
| `QWEN_BASE_URL` | 通义千问 API 基础 URL |
| `QWEN_MODEL` | Qwen 模型名称 |

### 注意事项

**⚠️ 安全提示：** 请勿将包含真实 API 密钥的配置文件提交到版本控制系统中。建议使用环境变量或本地配置文件来管理敏感信息。

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

## 扩展新模型

要添加新的 AI 模型支持：

1. 在 `application.yaml` 中添加配置：
   ```yaml
   spring:
     ai:
       newmodel:
         enabled: true
         base-url: https://api.newmodel.com
         api-key: ${NEWMODEL_API_KEY:}
         model: model-name
   ```

2. 在 `MultiModelAiConfig.java` 中添加新的 Bean：
   ```java
   @Bean
   @Qualifier("newmodelChatClient")
   @ConditionalOnProperty(prefix = "spring.ai.newmodel", name = "enabled", havingValue = "true")
   public ChatClient newmodelChatClient() {
       OpenAiApi api = new OpenAiApi(
           System.getenv("NEWMODEL_BASE_URL"),
           System.getenv("NEWMODEL_API_KEY")
       );
       return new OpenAiChatClient(api,
           OpenAiChatOptions.builder().withModel("model-name").build());
   }
   ```

3. 在 `ChatController` 和 `DemoRunner` 中注册新的模型映射。

## 错误处理

### 常见问题

1. **API 密钥错误**
   - 确保环境变量已正确设置
   - 检查 API 密钥是否有效

2. **网络连接问题**
   - 检查网络连接
   - 确认可以访问对应的 API 端点

3. **模型不支持错误**
   - 检查配置的模型名称是否正确
   - 确认该模型在服务商的账户中可用

## 环境要求

- Java 21 或更高版本
- Gradle 9.3+

## 许可证

Mozilla Public License 2.0
