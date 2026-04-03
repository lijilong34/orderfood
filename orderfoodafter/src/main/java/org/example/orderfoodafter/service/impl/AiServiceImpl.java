package org.example.orderfoodafter.service.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.example.orderfoodafter.common.DefaulteProperties;
import org.example.orderfoodafter.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务实现类
 * 根据是否有图片自动选择使用智谱AI或langchain4j
 *
 * @author iFlow
 * @date 2025-03-15
 */
@Service
public class AiServiceImpl implements AiService {

    // 注入 langchain4j 的 ChatLanguageModel（非流式）
    private final ChatLanguageModel chatLanguageModel;

    // 注入 langchain4j 的 StreamingChatModel（流式）
    private final OpenAiStreamingChatModel streamingChatModel;

    // 注入配置
    @Autowired
    private DefaulteProperties defaulteProperties;

    // 智谱AI客户端
    private static final ZhipuAiClient zhipuClient = ZhipuAiClient.builder()
            .apiKey("")
            .build();

    // 智谱AI图片识别客户端
    private static final ZhipuAiClient imageClient = ZhipuAiClient.builder()
            .apiKey("")
            .build();
/**
 * AiServiceImpl方法
 *
 * @author 李吉隆
 */

    public AiServiceImpl(@Value("${langchain4j.open-ai.chat-model.api-key}") String apiKey,
                         @Value("${langchain4j.open-ai.chat-model.model-name}") String modelName,
                         @Value("${langchain4j.open-ai.chat-model.base-url}") String baseUrl) {
        // 创建 langchain4j 的 ChatLanguageModel（非流式）
        this.chatLanguageModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .timeout(java.time.Duration.ofMinutes(5)) // 设置5分钟超时
                .build();

        // 创建 langchain4j 的 StreamingChatModel（流式）
        this.streamingChatModel = OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .timeout(java.time.Duration.ofMinutes(5)) // 设置5分钟超时
                .build();
    }
/**
 * chat方法
 *
 * @author 李吉隆
 */

    @Override
    public String chat(List<ChatMessage> messages, boolean hasImage, List<ChatTool> tools) throws Exception {
        if (hasImage) {
            // 有图片，使用智谱AI（glm-4.6v-flash支持图片）
                /**
     * chatWithZhipuAi
     * 
     * @author 李吉隆
     */
            return chatWithZhipuAi(messages, tools);
        } else {
            // 无图片，使用 langchain4j（qwen3-coder-plus）
            // 将 ChatMessage 转换为 langchain4j 的 UserMessage
                /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
            StringBuilder textContent = new StringBuilder();
            for (ChatMessage msg : messages) {
                if (msg.getRole().equals("user") || msg.getRole().equals("assistant") || msg.getRole().equals("system")) {
                    if (msg.getContent() instanceof String) {
                        textContent.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
                    }
                }
            }
                /**
     * chatWithLangChain4j
     * 
     * @author 李吉隆
     */
            return chatWithLangChain4j(List.of(dev.langchain4j.data.message.UserMessage.from(textContent.toString())));
        }
    }
/**
 * chatWithLangChain4j方法
 *
 * @author 李吉隆
 */

    @Override
    public String chatWithLangChain4j(List<dev.langchain4j.data.message.UserMessage> messages) throws Exception {
        if (messages.isEmpty()) {
                /**
     * IllegalArgumentException
     * 
     * @author 李吉隆
     */
            throw new IllegalArgumentException("消息列表不能为空");
        }

        // 使用 langchain4j 调用AI，提取文本并调用 chat(String) 方法
        String userText = messages.get(0).singleText();
        return chatLanguageModel.chat(userText);
    }
/**
 * chatWithLangChain4jStream方法
 *
 * @author 李吉隆
 */

    @Override
    public void chatWithLangChain4jStream(List<UserMessage> messages, dev.langchain4j.model.chat.response.StreamingChatResponseHandler handler) throws Exception {
        if (messages.isEmpty()) {
                /**
     * IllegalArgumentException
     * 
     * @author 李吉隆
     */
            throw new IllegalArgumentException("消息列表不能为空");
        }

        // 使用 langchain4j 流式调用AI
        String userText = messages.get(0).singleText();
        streamingChatModel.chat(userText, handler);
    }
/**
 * chatWithLangChain4jWithTools方法
 *
 * @author 李吉隆
 */

    @Override
    public String chatWithLangChain4jWithTools(List<UserMessage> messages, List<ToolSpecification> tools) throws Exception {
        if (messages.isEmpty()) {
                /**
     * IllegalArgumentException
     * 
     * @author 李吉隆
     */
            throw new IllegalArgumentException("消息列表不能为空");
        }

        // 使用 langchain4j 调用AI，支持工具
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(messages.stream().map(msg -> (dev.langchain4j.data.message.ChatMessage) msg).toList())
                .toolSpecifications(tools)
                .build();

        var response = chatLanguageModel.chat(chatRequest);
        AiMessage aiMessage = response.aiMessage();
        
        // 检查是否有工具调用请求
        if (aiMessage.hasToolExecutionRequests()) {
            // 有工具调用，返回工具调用信息
                /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
            StringBuilder toolCalls = new StringBuilder();
            toolCalls.append("AI请求调用以下工具：\n");
            for (dev.langchain4j.agent.tool.ToolExecutionRequest toolRequest : aiMessage.toolExecutionRequests()) {
                toolCalls.append("- ").append(toolRequest.name()).append(": ").append(toolRequest.arguments()).append("\n");
            }
            return toolCalls.toString();
        } else {
            // 没有工具调用，返回文本响应
            return aiMessage.text();
        }
    }
/**
 * chatWithZhipuAi方法
 *
 * @author 李吉隆
 */

    @Override
    public String chatWithZhipuAi(List<ChatMessage> messages, List<ChatTool> tools) throws Exception {
        if (messages.isEmpty()) {
                /**
     * IllegalArgumentException
     * 
     * @author 李吉隆
     */
            throw new IllegalArgumentException("消息列表不能为空");
        }

        // 创建请求参数
        ChatCompletionCreateParams request;
        if (tools != null && !tools.isEmpty()) {
            request = ChatCompletionCreateParams.builder()
                    .model("glm-4.6v-flash")
                    .messages(messages)
                    .tools(tools)
                    .build();
        } else {
            request = ChatCompletionCreateParams.builder()
                    .model("glm-4.6v-flash")
                    .messages(messages)
                    .build();
        }

        ChatCompletionResponse response = zhipuClient.chat().createChatCompletion(request);

        if (response.isSuccess()) {
            ChatMessage message = response.getData().getChoices().get(0).getMessage();
            Object reply = message.getContent();
            if (reply instanceof String) {
                return (String) reply;
            }
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("智谱AI返回的数据格式不正确");
        } else {
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("智谱AI调用失败: " + response.getMsg());
        }
    }
/**
 * chatWithZhipuAiWithResponse方法
 *
 * @author 李吉隆
 */

    @Override
    public ChatCompletionResponse chatWithZhipuAiWithResponse(List<ChatMessage> messages, List<ChatTool> tools) throws Exception {
        if (messages.isEmpty()) {
                /**
     * IllegalArgumentException
     * 
     * @author 李吉隆
     */
            throw new IllegalArgumentException("消息列表不能为空");
        }

        // 创建请求参数
        ChatCompletionCreateParams request;
        if (tools != null && !tools.isEmpty()) {
            request = ChatCompletionCreateParams.builder()
                    .model("glm-4.6v-flash")
                    .messages(messages)
                    .tools(tools)
                    .build();
        } else {
            request = ChatCompletionCreateParams.builder()
                    .model("glm-4.6v-flash")
                    .messages(messages)
                    .build();
        }

        // 调用智谱AI，返回完整响应
        ChatCompletionResponse response = zhipuClient.chat().createChatCompletion(request);

        if (response.isSuccess()) {
            return response;
        } else {
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("智谱AI调用失败: " + response.getMsg());
        }
    }
/**
 * getImageDescription方法
 *
 * @author 李吉隆
 */

    @Override
    public String getImageDescription(String imgname) throws Exception {
        if (imgname == null || imgname.trim().isEmpty()) {
            return "用户未上传图片";
        }

        String filepath = defaulteProperties.getUploadfilepath() + imgname;
            /**
     * readAllBytes
     * 
     * @author 李吉隆
     */
        byte[] bytes = Files.readAllBytes(new File(filepath).toPath());
        String base64 = Base64.getEncoder().encodeToString(bytes);

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model("glm-4.6v-flash")
                .messages(List.of(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content(List.of(
                                        MessageContent.builder()
                                                .type("text")
                                                .text("请详细描述这张图片中的食物或菜品。包括：1) 食物名称 2) 主要食材 3) 烹饪方式 4) 大致分量。只输出描述内容，不要包含其他文字。")
                                                .build(),
                                        MessageContent.builder()
                                                .type("image_url")
                                                .imageUrl(ImageUrl.builder()
                                                        .url(base64)
                                                        .build())
                                                .build()))
                                .build()))
                .build();

        ChatCompletionResponse response = imageClient.chat().createChatCompletion(request);
        Object reply = null;
        if (response.isSuccess()) {
            ChatMessage message = response.getData().getChoices().get(0).getMessage();
            reply = message.getContent();
        } else {
            System.err.println("错误: " + response.getMsg());
        }

        if (reply == null) {
            return "图片描述获取失败";
        }

        return reply.toString();
    }

    @Override
        /**
     * executeToolLoop
     * 
     * @author 李吉隆
     */
    public String executeToolLoop(List<dev.langchain4j.data.message.ChatMessage> messages,
                                       List<ToolSpecification> tools,
                                       org.example.orderfoodafter.service.McpService mcpService,
                                       int maxRetries) {
                // 保存工具执行过程的列表，用于返回给前端显示
                List<String> toolExecutionProcess = new ArrayList<>();
    
                try {
                    int iteration = 0;
                    int maxIterations = 15; // 最大迭代次数限制，防止无限循环
                    int consecutiveFailures = 0; // 连续失败次数
                    String lastToolName = null; // 上次调用的工具名称
                    String lastToolArgs = null; // 上次调用的工具参数
                    int duplicateOperationCount = 0; // 重复操作计数        // 添加中文系统提示，确保AI能够理解中文指令并正确执行工具调用
        List<dev.langchain4j.data.message.ChatMessage> enhancedMessages = new ArrayList<>();
        enhancedMessages.add(new dev.langchain4j.data.message.SystemMessage(
            "你是一个智能助手，能够使用MCP工具来控制浏览器在当前点餐系统内执行各种任务。\n\n" +
            "【重要规则】\n" +
            "1. 对于用户的任何请求，首先考虑使用工具来完成，而不是直接拒绝\n" +
            "2. 绝不要说\"我无法访问\"或\"我不能\"，而是尝试用工具去完成\n" +
            "3. 【禁止】不要打开新的浏览器标签页\n" +
            "4. 【禁止】不要跳转到外部网站（如百度、外卖平台等）\n" +
            "5. 【禁止】不要使用 chrome_navigate 工具访问外部URL\n" +
            "6. 所有操作必须在当前点餐系统页面内完成\n" +
            "7. 【禁止】【绝对禁止】不要在selector参数中使用ref！ref是MCP工具的内部引用ID，不是HTML元素的属性，不能作为CSS选择器使用！\n" +
            "   错误示例：{\"selector\": \"button[ref='ref_41']\"} ❌ 这会导致点击失败！\n" +
            "   错误示例：{\"selector\": \"menuitem[ref='ref_9']\"} ❌ 这会导致点击失败！\n" +
            "   正确示例：{\"selector\": \"button.close\"} ✅\n" +
            "   正确示例：{\"selector\": \"#button-id\"} ✅\n" +
            "   正确示例：{\"selector\": \"//button[contains(@class, 'close')]\", \"selectorType\": \"xpath\"} ✅\n" +
            "   如果页面元素显示有ref，请忽略ref，根据元素的其他属性（如class、id、text、type）来构建selector！\n" +
            "8. 【禁止】【绝对禁止】不要在selector参数中使用x、y、x0、y0、x1、y1等坐标属性！这些是MCP工具返回的坐标信息，不是HTML元素的属性，不能作为CSS选择器使用！\n" +
            "   错误示例：{\"selector\": \"button[x='1876'][y='124']\"} ❌ 这会导致点击失败！\n" +
            "   错误示例：{\"selector\": \"generic[x='693'][y='355']\"} ❌ 这会导致点击失败！\n" +
            "   错误示例：{\"selector\": \"div[x0='960'][y0='460'][x1='1876'][y1='560']\"} ❌ 这会导致点击失败！\n" +
            "   正确示例：{\"selector\": \"button.close\"} ✅\n" +
            "   正确示例：{\"selector\": \"#button-id\"} ✅\n" +
            "   正确示例：{\"selector\": \"button:contains('关闭')\"} ✅\n" +
            "   正确示例：{\"selector\": \"//button[contains(@class, 'close')]\", \"selectorType\": \"xpath\"} ✅\n" +
            "   如果需要根据元素定位，请使用chrome_computer的coordinates参数，而不是selector参数！\n" +
            "9. 【重要】chrome_read_page返回的元素类型（如generic、menuitem等）是MCP工具的通用标签，不是HTML元素的实际标签名！\n" +
            "   错误示例：{\"selector\": \"generic[金光奶茶苑]\"} ❌ 这会导致点击失败！\n" +
            "   错误示例：{\"selector\": \"menuitem[所有分类]\"} ❌ 这会导致点击失败！\n" +
            "   正确示例：使用chrome_computer工具的coordinates参数点击：{\"action\": \"left_click\", \"coordinates\": {\"x\": 693, \"y\": 355}} ✅\n" +
            "   正确示例：如果元素有id，使用id：{\"selector\": \"#element-id\"} ✅\n" +
            "   正确示例：如果元素有class，使用class：{\"selector\": \".button-class\"} ✅\n" +
            "   如果需要根据坐标点击，必须使用chrome_computer工具，而不是chrome_click_element！\n" +
            "10. 请使用中文回复用户\n" +
                                        "11. 执行工具后，将结果用中文总结给用户\n" +
                                        "12. 【重要】每次只执行一个工具，不要尝试同时执行多个工具\n" +
                                        "13. 【重要】执行完一个工具后，等待系统反馈，再决定是否执行下一个工具\n" +
                                        "14. 【绝对禁止】不要在回复文本中写任何JSON格式的工具调用描述！\n" +
                                        "   例如：禁止写这样的内容：```json {\"action\": \"chrome_read_page\"}```\n" +
                                        "   如果你需要使用工具，直接调用工具即可，系统会自动处理。\n" +
                                        "   你的回复应该是纯文本描述你的意图，而不是包含工具调用的JSON代码块。\n\n" +            "【工具使用场景 - 仅限当前系统内】\n" +
            "- 查看当前页面内容 → 使用 chrome_read_page\n" +
            "- 截图当前页面 → 使用 chrome_screenshot\n" +
            "- 点击页面上的按钮/链接 → 使用 chrome_click_element\n" +
            "- 在页面输入框输入文本 → 使用 chrome_fill_or_select\n" +
            "- 滚动当前页面 → 使用 chrome_computer 的 scroll 操作\n" +
            "- 执行键盘操作 → 使用 chrome_keyboard\n" +
            "- 用户说\"帮我点奶茶\"、\"帮我下单\"等 → 在当前页面查找菜单、选择商品、添加到购物车\n\n" +
            "【操作流程】\n" +
            "1. 理解用户需求\n" +
            "2. 只在当前页面内操作，不要跳转\n" +
            "3. 使用 chrome_read_page 查看当前页面内容\n" +
            "4. 根据页面内容，使用 chrome_click_element 点击相关按钮\n" +
            "5. 根据执行结果继续操作或给用户回复\n" +
            "6. 分步骤执行并实时反馈给用户\n" +
            "7. 每次只执行一个工具，不要一次性完成所有步骤"
        ));
        enhancedMessages.addAll(messages);
        
        while (iteration < maxIterations) {
            iteration++;
            System.out.println("=== 工具调用循环 " + iteration + " (最大" + maxIterations + "次) ===");
            
            // 调用AI，使用enhancedMessages（包含所有历史消息和工具执行结果）
            ChatRequest request = ChatRequest.builder()
                    .messages(enhancedMessages)
                    .toolSpecifications(tools)
                    .build();
            
            AiMessage aiMessage = null;
            try {
                var response = chatLanguageModel.chat(request);
                aiMessage = response.aiMessage();
            } catch (Exception e) {
                System.err.println("AI调用失败: " + e.getMessage());
                // AI调用失败，如果有工具执行过程，返回工具执行过程
                if (!toolExecutionProcess.isEmpty()) {
                    System.out.println("AI调用失败，返回工具执行过程");
                        /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
                    StringBuilder finalResult = new StringBuilder();
                    finalResult.append("【工具执行过程】\n");
                    for (String process : toolExecutionProcess) {
                        finalResult.append(process).append("\n");
                    }
                    finalResult.append("\n【状态】\nAI处理完成");
                    return finalResult.toString();
                } else {
                    // 没有工具执行过程，返回简单的成功消息
                    return "操作完成";
                }
            }
            
            // 检查是否有工具调用请求
            if (!aiMessage.hasToolExecutionRequests()) {
                // 没有工具调用，AI给出最终回复
                System.out.println("AI给出最终回复，结束循环");
                enhancedMessages.add(aiMessage);
                
                // 合并工具执行过程和AI最终回复
                    /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
                StringBuilder finalResult = new StringBuilder();
                if (!toolExecutionProcess.isEmpty()) {
                    finalResult.append("【工具执行过程】\n");
                    for (String process : toolExecutionProcess) {
                        finalResult.append(process).append("\n");
                    }
                    finalResult.append("\n");
                }
                finalResult.append("【AI回复】\n").append(aiMessage.text());
                
                return finalResult.toString();
            }
            
            // 有工具调用，执行工具
            System.out.println("AI请求调用工具，工具数量: " + aiMessage.toolExecutionRequests().size());
            enhancedMessages.add(aiMessage);
            
            // 执行所有工具调用
            boolean allToolsSucceeded = true;
            for (dev.langchain4j.agent.tool.ToolExecutionRequest toolRequest : aiMessage.toolExecutionRequests()) {
                String toolName = toolRequest.name();
                String toolArgs = toolRequest.arguments();
                
                // 检测重复操作
                if (toolName.equals(lastToolName) && toolArgs.equals(lastToolArgs)) {
                    duplicateOperationCount++;
                    System.out.println("检测到重复操作: " + toolName + " (重复次数: " + duplicateOperationCount + ")");
                    
                    if (duplicateOperationCount >= 3) {
                        System.out.println("连续重复操作3次，强制停止");
                        
                        String errorMsg = "检测到连续重复操作3次，请尝试其他方法。重复的操作: " + toolName + ", 参数: " + toolArgs;
                        enhancedMessages.add(dev.langchain4j.data.message.ToolExecutionResultMessage.from(
                            toolRequest, errorMsg
                        ));
                        
                        // 让AI基于当前状态给出结果
                        break;
                    }
                } else {
                    // 操作不同，重置重复计数
                    duplicateOperationCount = 0;
                    lastToolName = toolName;
                    lastToolArgs = toolArgs;
                }
                
                System.out.println("\n【工具调用】执行工具: " + toolName + ", 参数: " + toolArgs);
                
                int retryCount = 0;
                boolean toolSucceeded = false;
                
                // 工具调用失败重试机制
                while (retryCount < maxRetries && !toolSucceeded) {
                    retryCount++;
                    
                        /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
                    StringBuilder executionLog = new StringBuilder();
                    executionLog.append("\n🔧 正在执行工具: ").append(toolName).append("\n");
                    executionLog.append("📝 参数: ").append(toolArgs).append("\n");
                    executionLog.append("🔄 重试次数: ").append(retryCount).append("/").append(maxRetries).append("\n");
                    
                    try {
                                            // 解析参数
                                            Map<String, Object> arguments = parseToolArguments(toolArgs);
                    
                                            // 对于截图工具，强制设置参数避免下载文件
                                            if ("chrome_screenshot".equals(toolName)) {
                                                arguments.put("savePng", false);
                                                arguments.put("storeBase64", true);
                                                executionLog.append("⚙️  已自动调整参数: savePng=false, storeBase64=true（避免下载文件）\n");
                                            }
                    
                                            // 【重要】检测selector参数是否包含ref或坐标，如果有就拒绝执行
                                            if ("chrome_click_element".equals(toolName) || "chrome_fill_or_select".equals(toolName)) {
                                                Object selectorObj = arguments.get("selector");
                                                if (selectorObj != null) {
                                                    String selectorStr = selectorObj.toString();
                                                    
                                                    // 检测ref
                                                    if (selectorStr.contains("[ref=")) {
                                                        String errorMsg = "❌ 拒绝执行：selector参数中包含了ref！ref是MCP工具的内部引用ID，不是HTML元素的属性。";
                                                        errorMsg += "请根据元素的实际HTML属性（如class、id、text、type）来构建selector。";
                                                        errorMsg += "例如：{\"selector\": \"button.close\"} 或 {\"selector\": \"#button-id\"}";
                                                        executionLog.append(errorMsg).append("\n");
                                                        System.out.println(errorMsg);
                                                        
                                                        // 返回错误信息给AI，让它重新选择正确的selector
                                                        String errorResult = executionLog.toString();
                                                        enhancedMessages.add(dev.langchain4j.data.message.ToolExecutionResultMessage.from(
                                                            toolRequest, errorResult
                                                        ));
                                                        
                                                        allToolsSucceeded = false;
                                                        consecutiveFailures++;
                                                        toolSucceeded = false;
                                                        continue; // 跳过这次执行，让AI重新尝试
                                                    }
                                                    
                                                    // 检测坐标属性
                                                    if (selectorStr.matches(".*\\[\\s*(x|y|x0|y0|x1|y1)\\s*=") || 
                                                        selectorStr.matches(".*\\[x=.*\\].*\\[y=.*\\]") ||
                                                        selectorStr.matches(".*\\[x0=.*\\].*\\[y0=.*\\]") ||
                                                        selectorStr.matches(".*\\[x1=.*\\].*\\[y1=.*\\]")) {
                                                        String errorMsg = "❌ 拒绝执行：selector参数中包含了坐标属性（x、y、x0、y0、x1、y1）！这些是MCP工具返回的坐标信息，不是HTML元素的属性。";
                                                        errorMsg += "请根据元素的实际HTML属性（如class、id、text、type）来构建selector。";
                                                        errorMsg += "例如：{\"selector\": \"button.close\"} 或 {\"selector\": \"#button-id\"}";
                                                        errorMsg += "如果需要根据元素定位，请使用chrome_computer的coordinates参数，而不是selector参数！";
                                                        executionLog.append(errorMsg).append("\n");
                                                        System.out.println(errorMsg);
                                                        
                                                        // 返回错误信息给AI，让它重新选择正确的selector
                                                        String errorResult = executionLog.toString();
                                                        enhancedMessages.add(dev.langchain4j.data.message.ToolExecutionResultMessage.from(
                                                            toolRequest, errorResult
                                                        ));
                                                        
                                                        allToolsSucceeded = false;
                                                        consecutiveFailures++;
                                                        toolSucceeded = false;
                                                        continue; // 跳过这次执行，让AI重新尝试
                                                    }
                                                }
                                            }
                    
                                            executionLog.append("⏳ 正在调用MCP服务...\n");
                    
                                            // 调用MCP工具
                                            Map<String, Object> mcpResult = mcpService.callTool(toolName, arguments);                        System.out.println("工具执行原始结果: " + mcpResult);
                        
                        // 将工具执行结果添加到对话历史
                        String resultContent = formatMcpResult(mcpResult);
                        System.out.println("工具执行格式化结果: " + resultContent);
                        
                        // 检查是否是截图工具，如果是则进行图片识别
                        if ("chrome_screenshot".equals(toolName) && mcpResult.containsKey("screenshotPath")) {
                            try {
                                String screenshotPath = mcpResult.get("screenshotPath").toString();
                                executionLog.append("📸 截图已保存，正在启动AI识别...\n");
                                System.out.println("检测到截图，开始进行图片识别: " + screenshotPath);
                                
                                // 调用图片识别
                                String imageDescription = getImageDescription(screenshotPath);
                                System.out.println("图片识别结果: " + imageDescription);
                                
                                // 将图片识别结果添加到工具执行结果中
                                resultContent += "\n\n🤖 【AI图片识别】\n" + imageDescription;
                                executionLog.append("✅ AI识别完成\n");
                            } catch (Exception e) {
                                System.err.println("图片识别失败: " + e.getMessage());
                                resultContent += "\n\n❌ 【AI图片识别失败】" + e.getMessage();
                                executionLog.append("❌ AI识别失败: ").append(e.getMessage()).append("\n");
                            }
                        }
                        
                        // 添加完整的执行日志到结果中
                        String fullResult = executionLog.toString() + resultContent;
                        
                        enhancedMessages.add(dev.langchain4j.data.message.ToolExecutionResultMessage.from(
                            toolRequest, fullResult
                        ));
                        
                        // 将执行过程保存到列表中，用于返回给前端
                        toolExecutionProcess.add("🔧 执行工具: " + toolName + "\n" + fullResult);
                        
                        executionLog.append("✅ 工具执行成功\n");
                        toolSucceeded = true;
                        
                    } catch (Exception e) {
                        executionLog.append("❌ 工具执行失败: ").append(e.getMessage()).append("\n");
                        System.err.println("工具执行失败 (重试 " + retryCount + "/" + maxRetries + "): " + e.getMessage());
                        
                        if (retryCount < maxRetries) {
                            // 还有重试机会，继续重试
                            executionLog.append("⏳ 等待重试...\n");
                            continue;
                        } else {
                            // 重试次数用完，记录失败
                            allToolsSucceeded = false;
                            consecutiveFailures++;
                            executionLog.append("⚠️  已达到最大重试次数，停止重试\n");
                            
                            String errorMessage = executionLog.toString();
                            enhancedMessages.add(dev.langchain4j.data.message.ToolExecutionResultMessage.from(
                                toolRequest, errorMessage
                            ));
                            
                            // 将失败过程也保存到列表中
                            toolExecutionProcess.add("❌ 工具执行失败: " + toolName + "\n" + errorMessage);
                            
                            System.out.println("连续失败次数: " + consecutiveFailures + "次");
                            
                            // 检查是否达到连续失败次数
                            if (consecutiveFailures >= 5) {
                                System.out.println("工具连续失败" + consecutiveFailures + "次，停止迭代");
                                
                                // 让AI基于当前状态给出结果
                                try {
                                    ChatRequest finalRequest = ChatRequest.builder()
                                            .messages(enhancedMessages)
                                            .build();
                                    
                                    var finalResponse = chatLanguageModel.chat(finalRequest);
                                    AiMessage finalAiMessage = finalResponse.aiMessage();
                                    enhancedMessages.add(finalAiMessage);
                                    
                                    // 合并工具执行过程和AI最终回复
                                        /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
                                    StringBuilder finalResult = new StringBuilder();
                                    if (!toolExecutionProcess.isEmpty()) {
                                        finalResult.append("【工具执行过程】\n");
                                        for (String process : toolExecutionProcess) {
                                            finalResult.append(process).append("\n");
                                        }
                                        finalResult.append("\n");
                                    }
                                    finalResult.append("【AI回复】\n").append(finalAiMessage.text());
                                    
                                    return finalResult.toString();
                                } catch (Exception ex) {
                                    System.err.println("获取AI最终结果失败: " + ex.getMessage());
                                    // 如果AI调用失败，返回基于已有对话历史的结果
                                    for (int i = messages.size() - 1; i >= 0; i--) {
                                        dev.langchain4j.data.message.ChatMessage msg = messages.get(i);
                                        if (msg instanceof dev.langchain4j.data.message.AiMessage) {
                                                /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
                                            StringBuilder finalResult = new StringBuilder();
                                            if (!toolExecutionProcess.isEmpty()) {
                                                finalResult.append("【工具执行过程】\n");
                                                for (String process : toolExecutionProcess) {
                                                    finalResult.append(process).append("\n");
                                                }
                                                finalResult.append("\n");
                                            }
                                            finalResult.append("【AI回复】\n").append(((dev.langchain4j.data.message.AiMessage) msg).text());
                                            
                                            return finalResult.toString();
                                        }
                                    }
                                    return "工具执行失败" + maxRetries + "次，无法完成操作。";
                                }
                            }
                        }
                    }
                }
            }
            
            // 限制对话历史长度
            if (messages.size() > 20) {
                enhancedMessages = messages.subList(messages.size() - 20, messages.size());
            }
            
            // 检查是否所有工具都失败了
            if (!allToolsSucceeded) {
                System.out.println("工具执行失败，让AI使用其他方案");
                // 在下一轮调用中不提供工具，让AI使用其他方案
                tools = null;
            }
            // 返回工具执行过程
            if (!toolExecutionProcess.isEmpty()) {
                StringBuilder finalResult = new StringBuilder();
                finalResult.append("【工具执行过程】\n");
                for (String process : toolExecutionProcess) {
                    finalResult.append(process).append("\n");
                }
                finalResult.append("\n【状态】\n操作完成");
                return finalResult.toString();
            }
            return "操作完成";
        } // while循环结束
        } // try块结束
        catch (Exception e) {
            System.err.println("executeToolLoop执行异常: " + e.getMessage());
            e.printStackTrace();
            // 即使发生异常，也尝试返回工具执行过程
            if (toolExecutionProcess != null && !toolExecutionProcess.isEmpty()) {
                StringBuilder finalResult = new StringBuilder();
                finalResult.append("【工具执行过程】\n");
                for (String process : toolExecutionProcess) {
                    finalResult.append(process).append("\n");
                }
                finalResult.append("\n【状态】\n操作完成");
                return finalResult.toString();
            } else {
                // 没有工具执行过程，返回错误消息
                return "操作过程中发生异常: " + e.getMessage();
            }
        }
        return "操作完成";
    }

    @Override
        /**
     * executeToolLoopWithSSE
     * 
     * @author 李吉隆
     */
    public void executeToolLoopWithSSE(SseEmitter emitter, String message,
                                        List<ToolSpecification> tools,
                                        org.example.orderfoodafter.service.McpService mcpService,
                                        int maxRetries) throws Exception {
        try {
            // 构建对话消息
            List<dev.langchain4j.data.message.ChatMessage> enhancedMessages = new ArrayList<>();
            enhancedMessages.add(new dev.langchain4j.data.message.SystemMessage(
                "你是一个智能助手，能够使用MCP工具来控制浏览器在当前点餐系统内执行各种任务，并且能够分析用户上传的图片内容。\n\n" +
                "【任务类型判断】\n" +
                "在处理用户请求前，首先判断任务类型：\n\n" +
                "【类型1：营养分析任务 - 不使用工具】\n" +
                "如果用户消息包含以下关键词，表示是营养分析任务，直接基于图片描述回答，不要使用任何工具：\n" +
                "- 营养价值、营养成分、营养分析\n" +
                "- 热量、卡路里、蛋白质、脂肪、碳水化合物\n" +
                "- 健康饮食、减肥、营养建议\n" +
                "- 如果消息以'用户上传的图片描述:'开头且用户询问食物相关问题\n\n" +
                "对于营养分析任务：\n" +
                "1. 直接基于图片描述中的食物信息回答\n" +
                "2. 提供专业、详细的营养建议\n" +
                "3. 不要使用任何浏览器工具\n" +
                "4. 不要尝试在页面上查找食物信息\n\n" +
                "【类型2：点餐操作任务 - 使用工具】\n" +
                "如果用户要求在点餐系统中执行操作，使用MCP工具：\n" +
                "- 帮我点XXX、帮我下单\n" +
                "- 查找商品、查看商家\n" +
                "- 添加到购物车\n" +
                "- 修改个人信息、查看订单等\n\n" +
                "【图片处理规则】\n" +
                "1. 如果用户消息以'用户上传的图片描述:'开头，说明用户上传了图片，请仔细阅读图片描述内容\n" +
                "2. 根据图片描述内容回答用户的问题，例如分析营养价值、识别食物等\n" +
                "3. 如果图片描述中提到了食物，可以直接基于描述信息回答，不要要求用户再次提供食物名称\n\n" +
                "【重要规则】\n" +
                "1. 对于点餐操作任务，优先使用工具来完成，而不是直接拒绝\n" +
                "2. 对于营养分析任务，不要使用任何工具，直接回答\n" +
                "3. 绝不要说\"我无法访问\"或\"我不能\"，而是尝试用工具去完成\n" +
                "4. 【禁止】不要打开新的浏览器标签页\n" +
                "5. 【禁止】不要跳转到外部网站（如百度、外卖平台等）\n" +
                "6. 【禁止】不要使用 chrome_navigate 工具访问外部URL\n" +
                "7. 所有操作必须在当前点餐系统页面内完成\n" +
                "8. 【禁止】【绝对禁止】不要退出登录！如果用户已经登录，绝对不要点击退出登录按钮！\n" +
                "   即使页面上显示\"退出登录\"按钮，也绝对不要点击它！\n" +
                "   如果不小心退出了登录，用户的会话和数据都会丢失。\n" +
                "9. 【禁止】【绝对禁止】不要在selector参数中使用ref！ref是MCP工具的内部引用ID，不是HTML元素的属性，不能作为CSS选择器使用！\n" +
                            "   错误示例：{\"selector\": \"button[ref='ref_41']\"} ❌ 这会导致点击失败！\n" +
                            "   错误示例：{\"selector\": \"menuitem[ref='ref_9']\"} ❌ 这会导致点击失败！\n" +
                            "   正确示例：{\"selector\": \"button.close\"} ✅\n" +
                            "   正确示例：{\"selector\": \"#button-id\"} ✅\n" +
                            "   正确示例：{\"selector\": \"//button[contains(@class, 'close')]\", \"selectorType\": \"xpath\"} ✅\n" +
                            "   如果页面元素显示有ref，请忽略ref，根据元素的其他属性（如class、id、text、type）来构建selector！\n" +
                            "10. 【禁止】【绝对禁止】不要在selector参数中使用x、y、x0、y0、x1、y1等坐标属性！这些是MCP工具返回的坐标信息，不是HTML元素的属性，不能作为CSS选择器使用！\n" +
                            "   错误示例：{\"selector\": \"button[x='1876'][y='124']\"} ❌ 这会导致点击失败！\n" +
                            "   错误示例：{\"selector\": \"generic[x='693'][y='355']\"} ❌ 这会导致点击失败！\n" +
                            "   错误示例：{\"selector\": \"div[x0='960'][y0='460'][x1='1876'][y1='560']\"} ❌ 这会导致点击失败！\n" +
                            "   正确示例：{\"selector\": \"button.close\"} ✅\n" +
                            "   正确示例：{\"selector\": \"#button-id\"} ✅\n" +
                            "   正确示例：{\"selector\": \"button:contains('关闭')\"} ✅\n" +
                            "   正确示例：{\"selector\": \"//button[contains(@class, 'close')]\", \"selectorType\": \"xpath\"} ✅\n" +
                            "   如果需要根据元素定位，请使用chrome_computer的coordinates参数，而不是selector参数！\n" +
                            "11. 【重要】chrome_read_page返回的元素类型（如generic、menuitem等）是MCP工具的通用标签，不是HTML元素的实际标签名！\n" +
                            "   错误示例：{\"selector\": \"generic[金光奶茶苑]\"} ❌ 这会导致点击失败！\n" +
                            "   错误示例：{\"selector\": \"menuitem[所有分类]\"} ❌ 这会导致点击失败！\n" +
                            "   正确示例：使用chrome_computer工具的coordinates参数点击：{\"action\": \"left_click\", \"coordinates\": {\"x\": 693, \"y\": 355}} ✅\n" +
                            "   正确示例：如果元素有id，使用id：{\"selector\": \"#element-id\"} ✅\n" +
                            "   正确示例：如果元素有class，使用class：{\"selector\": \".button-class\"} ✅\n" +
                            "   如果需要根据坐标点击，必须使用chrome_computer工具，而不是chrome_click_element！\n" +
                            "12. 请使用中文回复用户\n" +
                            "13. 执行工具后，将结果用中文总结给用户\n" +
                            "14. 【重要】每次只执行一个工具，不要尝试同时执行多个工具\n" +
                            "15. 【重要】执行完一个工具后，等待系统反馈，再决定是否执行下一个工具\n" +
                            "16. 【重要】当你要使用工具时，必须在文本中说明工具名称和参数！\n" +
                            "   格式：在文本中说明'调用XXX工具，参数：{JSON格式的参数}'\n" +
                            "   例如：调用chrome_click_element工具，参数：{\"selector\": \"button.close\"}\n" +
                            "   例如：调用chrome_fill_or_select工具，参数：{\"selector\": \"#username\", \"value\": \"admin\"}\n" +
                            "   系统会自动解析你的文本，提取工具名称和参数并执行。\n" +
                            "   如果工具不需要参数，可以省略参数部分。\n\n" +
                "【关键规则】当你要使用工具时，必须在文本中说明工具名称和参数！\n" +
                "系统会自动从你的文本中提取工具名称和参数并执行。\n\n" +
                "【工具使用场景 - 仅限点餐操作任务】\n" +
                "- 查看当前页面内容 → 调用chrome_read_page工具\n" +
                "- 截图当前页面 → 调用chrome_screenshot工具\n" +
                "- 点击页面上的按钮/链接 → 调用chrome_click_element工具\n" +
                "- 在页面输入框输入文本 → 调用chrome_fill_or_select工具\n" +
                "- 滚动当前页面 → 调用chrome_computer工具\n" +
                "- 执行键盘操作 → 调用chrome_keyboard工具\n" +
                "- 用户说\"帮我点奶茶\"、\"帮我下单\"等 → 调用相关工具在当前页面查找菜单、选择商品、添加到购物车\n" +
                "- 用户说\"修改个人信息\"、\"查看个人信息\"、\"我的资料\"、\"个人中心\"等 → 先点击\"我的\"页面进入个人中心，再进行相应操作\n" +
                "- 用户说\"查看余额\"、\"充值\"、\"我的订单\"等 → 先进入\"我的\"页面，再查看相应的功能模块\n\n" +
                "【操作流程】\n" +
                "1. 理解用户需求\n" +
                "2. 检查是否有图片描述，如果有，先分析图片描述内容\n" +
                "3. 只在当前页面内操作，不要跳转\n" +
                "4. 使用 chrome_read_page 查看当前页面内容\n" +
                "5. 根据页面内容，使用 chrome_click_element 点击相关按钮\n" +
                "6. 根据执行结果继续操作或给用户回复\n" +
                "7. 分步骤执行并实时反馈给用户\n" +
                "8. 每次只执行一个工具，不要一次性完成所有步骤"
            ));
            enhancedMessages.add(dev.langchain4j.data.message.UserMessage.from(message));

            int iteration = 0;
            boolean allToolsSucceeded = true;

            while (true) {
                iteration++;
                System.out.println("=== 工具调用循环 " + iteration + " ===");

                // 推送循环开始事件
                emitter.send(SseEmitter.event().name("loop_start").data("第 " + iteration + " 轮循环"));

                // 调用AI
                System.out.println("准备调用AI，消息数量: " + enhancedMessages.size());
                System.out.println("工具数量: " + tools.size());
                
                ChatRequest request = ChatRequest.builder()
                        .messages(enhancedMessages)
                        .toolSpecifications(tools)
                        .build();

                AiMessage aiMessage = null;
                try {
                    System.out.println("开始调用chatLanguageModel.chat()...");
                    var response = chatLanguageModel.chat(request);
                    aiMessage = response.aiMessage();
                    System.out.println("AI调用成功");
                } catch (Exception e) {
                    System.err.println("AI调用失败: " + e.getMessage());
                    e.printStackTrace();
                    emitter.send(SseEmitter.event().name("error").data("AI调用失败: " + e.getMessage()));
                    emitter.complete();
                    return;
                }

                System.out.println("检查AI回复内容...");
                System.out.println("AI是否有工具调用请求: " + aiMessage.hasToolExecutionRequests());
                System.out.println("AI文本内容: " + aiMessage.text());
                
                // 检查是否有工具调用请求
                if (!aiMessage.hasToolExecutionRequests()) {
                    // 没有Function Calling，尝试从文本中解析工具调用
                    System.out.println("没有Function Calling，尝试从文本中解析工具调用...");
                    
                    // 解析AI文本中的工具调用
                    List<Map<String, Object>> parsedTools = parseToolCallsFromText(aiMessage.text(), tools);
                    
                    if (parsedTools.isEmpty()) {
                        // 确实没有工具调用，AI给出最终回复
                        System.out.println("AI给出最终回复，结束循环");
                        emitter.send(SseEmitter.event().name("final_reply").data(aiMessage.text()));
                        emitter.complete();
                        return;
                    }
                    
                    // 执行解析出的工具
                    for (Map<String, Object> toolCall : parsedTools) {
                        String toolName = (String) toolCall.get("name");
                        Map<String, Object> arguments = (Map<String, Object>) toolCall.get("arguments");
                        
                        System.out.println("从文本解析出工具调用: " + toolName);
                        System.out.println("工具参数: " + arguments);
                        
                        // 推送工具调用开始事件
                        emitter.send(SseEmitter.event().name("tool_start")
                                .data("执行工具: " + toolName + "\n参数: " + arguments));
                        
                        // 执行工具
                        executeTool(toolName, arguments, emitter, enhancedMessages, mcpService);
                    }
                    
                    // 将AI消息添加到历史
                    enhancedMessages.add(aiMessage);
                    
                    // 继续下一轮循环
                    continue;
                }

                // 有Function Calling，执行工具
                List<dev.langchain4j.agent.tool.ToolExecutionRequest> toolRequests = aiMessage.toolExecutionRequests();
                enhancedMessages.add(aiMessage);
                
                for (dev.langchain4j.agent.tool.ToolExecutionRequest toolRequest : toolRequests) {
                    String toolName = toolRequest.name();
                    String argumentsJson = toolRequest.arguments();

                    System.out.println("AI请求执行工具: " + toolName);
                    System.out.println("工具参数: " + argumentsJson);

                    // 推送工具调用开始事件
                    emitter.send(SseEmitter.event().name("tool_start")
                            .data("执行工具: " + toolName + "\n参数: " + argumentsJson));

                    try {
                        // 解析参数
                        Map<String, Object> arguments = parseToolArguments(argumentsJson);
                        
                        // 执行工具
                        executeTool(toolName, arguments, emitter, enhancedMessages, mcpService);
                    } catch (Exception e) {
                        System.err.println("工具执行异常: " + e.getMessage());
                        e.printStackTrace();
                        emitter.send(SseEmitter.event().name("tool_error").data("工具执行失败: " + e.getMessage()));
                    }
                }
                
                // 继续下一轮循环
                continue;
            }
        } catch (Exception e) {
            System.err.println("executeToolLoopWithSSE执行异常: " + e.getMessage());
            e.printStackTrace();
            try {
                emitter.send(SseEmitter.event().name("error").data("执行异常: " + e.getMessage()));
                emitter.completeWithError(e);
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        }
    }

    /**
     * 解析工具参数
     */
    private Map<String, Object> parseToolArguments(String argsStr) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(argsStr);
            return mapper.convertValue(node, Map.class);
        } catch (Exception e) {
            System.err.println("解析工具参数失败: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * 格式化MCP结果 - 返回用户友好的执行过程信息
     */
    private String formatMcpResult(Map<String, Object> mcpResult) {
        if (mcpResult == null) {
            return "【工具执行】工具执行完成，但未返回结果";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("\n【工具执行结果】\n");
        
        // 检查是否有错误
        Object isError = mcpResult.get("isError");
        if (isError != null && Boolean.TRUE.equals(isError)) {
            result.append("❌ 执行失败\n");
            
            Object content = mcpResult.get("content");
            if (content != null) {
                result.append("错误信息: ").append(content).append("\n");
            }
        } else {
            result.append("✅ 执行成功\n");

            // 处理content内容
            Object content = mcpResult.get("content");
            if (content != null) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    String cleanedContent = "";
                    
                    if (content instanceof java.util.List) {
                        // 处理List<TextContent>的情况
                        java.util.List<?> contentList = (java.util.List<?>) content;
                        for (Object item : contentList) {
                            // 尝试获取text字段
                            String textStr = null;
                            
                            // 方法1：尝试通过反射获取text字段
                            try {
                                java.lang.reflect.Field textField = item.getClass().getDeclaredField("text");
                                textField.setAccessible(true);
                                Object textValue = textField.get(item);
                                if (textValue != null) {
                                    textStr = textValue.toString();
                                }
                            } catch (Exception e) {
                                // 反射失败，尝试其他方法
                            }
                            
                            // 方法2：尝试通过Map获取text字段
                            if (textStr == null && item instanceof java.util.Map) {
                                java.util.Map<?, ?> textContentMap = (java.util.Map<?, ?>) item;
                                Object textObj = textContentMap.get("text");
                                if (textObj != null) {
                                    textStr = textObj.toString();
                                }
                            }
                            
                            // 方法3：如果是TextContent对象，转换为JSON再提取text
                            if (textStr == null) {
                                String itemJson = mapper.writeValueAsString(item);
                                com.fasterxml.jackson.databind.JsonNode itemNode = mapper.readTree(itemJson);
                                if (itemNode.has("text")) {
                                    textStr = itemNode.get("text").asText();
                                }
                            }
                            
                            if (textStr != null) {
                                // 【关键】检查textStr是否是JSON格式
                                if (textStr.trim().startsWith("{") || textStr.trim().startsWith("[")) {
                                    try {
                                        // 解析JSON字符串
                                        com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(textStr);
                                        // 递归移除所有ref字段
                                        removeRefFields(rootNode);
                                        // 转回字符串
                                        cleanedContent += mapper.writeValueAsString(rootNode);
                                    } catch (Exception jsonEx) {
                                        // JSON解析失败，使用原始内容
                                        cleanedContent += textStr;
                                    }
                                } else {
                                    // 不是JSON格式，直接使用
                                    cleanedContent += textStr;
                                }
                            } else {
                                cleanedContent += item.toString();
                            }
                        }
                    } else {
                        // 处理单个content对象
                        String contentStr = content.toString();
                        // 【关键】检查contentStr是否是JSON格式
                        if (contentStr.trim().startsWith("{") || contentStr.trim().startsWith("[")) {
                            try {
                                // 解析JSON字符串
                                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(contentStr);
                                // 递归移除所有ref字段
                                removeRefFields(rootNode);
                                // 转回字符串
                                cleanedContent = mapper.writeValueAsString(rootNode);
                            } catch (Exception jsonEx) {
                                // JSON解析失败，使用原始内容
                                cleanedContent = contentStr;
                            }
                        } else {
                            // 不是JSON格式，直接使用
                            cleanedContent = contentStr;
                        }
                    }
                    
                    result.append("返回数据: ").append(cleanedContent).append("\n");
                } catch (Exception e) {
                    // 如果解析失败，使用原始内容
                    result.append("返回数据: ").append(content.toString()).append("\n");
                }
            }
        }
        
        // 处理截图信息
        Object screenshotPath = mcpResult.get("screenshotPath");
        if (screenshotPath != null) {
            result.append("\n📸 截图已保存: ").append(screenshotPath).append("\n");
        }
        
        Object screenshotFullpath = mcpResult.get("screenshotFullpath");
        if (screenshotFullpath != null) {
            result.append("📁 完整路径: ").append(screenshotFullpath).append("\n");
        }
        
        return result.toString();
    }
    
    /**
     * 递归移除JSON中的所有ref字段
     * @param node JSON节点
     */
    private void removeRefFields(com.fasterxml.jackson.databind.JsonNode node) {
        if (node.isObject()) {
            // 移除当前对象中的ref字段
            if (node.has("ref")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) node).remove("ref");
            }
            // 移除refId字段
            if (node.has("refId")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) node).remove("refId");
            }
            
            // 【重要】处理pageContent字段中的ref字符串
            if (node.has("pageContent")) {
                com.fasterxml.jackson.databind.JsonNode pageContentNode = node.get("pageContent");
                if (pageContentNode.isTextual()) {
                    String pageContent = pageContentNode.asText();
                    // 使用正则表达式移除所有的 [ref=ref_X] 模式
                    String cleanedPageContent = pageContent.replaceAll("\\s*\\[ref=ref_\\d+\\]", "");
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("pageContent", cleanedPageContent);
                }
            }
            
            // 递归处理所有子节点
            java.util.Iterator<java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode> entry = fields.next();
                removeRefFields(entry.getValue());
            }
        } else if (node.isArray()) {
            // 递归处理数组中的每个元素
            for (com.fasterxml.jackson.databind.JsonNode arrayElement : node) {
                removeRefFields(arrayElement);
            }
        }
    }
    
    /**
     * 从AI文本中解析工具调用
     */
    private List<Map<String, Object>> parseToolCallsFromText(String text, List<ToolSpecification> availableTools) {
        List<Map<String, Object>> toolCalls = new ArrayList<>();
        
        System.out.println("开始解析AI文本中的工具调用...");
        System.out.println("文本内容: " + text);
        
        // 提取所有可用的工具名称
        java.util.Set<String> toolNames = new java.util.HashSet<>();
        for (ToolSpecification tool : availableTools) {
            toolNames.add(tool.name());
        }
        
        System.out.println("可用工具列表: " + toolNames);
        
        // 查找文本中提到的工具名称
        for (String toolName : toolNames) {
            // 查找工具名称（不区分大小写）
            if (text.toLowerCase().contains(toolName.toLowerCase())) {
                System.out.println("在文本中找到工具: " + toolName);
                
                Map<String, Object> toolCall = new HashMap<>();
                toolCall.put("name", toolName);
                
                // 解析参数
                Map<String, Object> arguments = new HashMap<>();
                
                // 查找工具名称后面的"参数："或"params:"
                int toolNameIndex = text.toLowerCase().indexOf(toolName.toLowerCase());
                if (toolNameIndex >= 0) {
                    int paramsStartIndex = -1;
                    
                    // 查找"参数："或"params:"（在工具名称后面）
                    String textAfterTool = text.substring(toolNameIndex + toolName.length());
                    
                    // 查找"参数："或"params:"关键词
                    int paramsKeywordIndex = textAfterTool.indexOf("参数：");
                    if (paramsKeywordIndex < 0) {
                        paramsKeywordIndex = textAfterTool.indexOf("params:");
                    }
                    
                    if (paramsKeywordIndex >= 0) {
                        paramsStartIndex = paramsKeywordIndex;
                        String textAfterParams = textAfterTool.substring(paramsKeywordIndex + 3); // 跳过"参数："或"params:"
                        
                        // 查找JSON格式的参数（从{开始）
                        int jsonStart = textAfterParams.indexOf('{');
                        if (jsonStart >= 0) {
                            // 查找对应的}
                            int jsonEnd = findMatchingBrace(textAfterParams, jsonStart);
                            if (jsonEnd >= 0) {
                                String jsonStr = textAfterParams.substring(jsonStart, jsonEnd + 1);
                                System.out.println("提取到的JSON参数: " + jsonStr);
                                
                                try {
                                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                                    arguments = mapper.readValue(jsonStr, Map.class);
                                    System.out.println("解析参数成功: " + arguments);
                                } catch (Exception e) {
                                    System.err.println("解析JSON参数失败: " + e.getMessage());
                                    // 使用空参数
                                }
                            }
                        }
                    }
                }
                
                toolCall.put("arguments", arguments);
                toolCalls.add(toolCall);
                
                System.out.println("添加工具调用: " + toolName + ", 参数: " + arguments);
            }
        }
        
        System.out.println("解析出 " + toolCalls.size() + " 个工具调用");
        return toolCalls;
    }
    
    /**
     * 查找匹配的右括号
     */
    private int findMatchingBrace(String str, int start) {
        int depth = 0;
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * 执行工具（将原有逻辑提取出来）
     */
    private void executeTool(String toolName, Map<String, Object> arguments, 
                            SseEmitter emitter, 
                            List<dev.langchain4j.data.message.ChatMessage> enhancedMessages,
                            org.example.orderfoodafter.service.McpService mcpService) throws Exception {
        
        // 【重要】检测selector参数是否包含ref或坐标，如果有就拒绝执行
        if ("chrome_click_element".equals(toolName) || "chrome_fill_or_select".equals(toolName)) {
            Object selectorObj = arguments.get("selector");
            if (selectorObj != null) {
                String selectorStr = selectorObj.toString();
            
                // 检测ref
                if (selectorStr.contains("[ref=")) {
                    String errorMsg = "❌ 拒绝执行：selector参数中包含了ref！ref是MCP工具的内部引用ID，不是HTML元素的属性。";
                    errorMsg += "请根据元素的实际HTML属性（如class、id、text、type）来构建selector。";
                    
                    emitter.send(SseEmitter.event().name("tool_error").data(errorMsg));
                    
                    // 添加工具执行结果为用户消息
                    enhancedMessages.add(dev.langchain4j.data.message.UserMessage.from(
                        "工具 " + toolName + " 执行结果: " + errorMsg
                    ));
                    return;
                }
            
                // 检测坐标属性
                if (selectorStr.matches(".*\\[\\s*(x|y|x0|y0|x1|y1)\\s*=") || 
                    selectorStr.matches(".*\\[x=.*\\].*\\[y=.*\\]") ||
                    selectorStr.matches(".*\\[x0=.*\\].*\\[y0=.*\\]") ||
                    selectorStr.matches(".*\\[x1=.*\\].*\\[y1=.*\\]")) {
                    String errorMsg = "❌ 拒绝执行：selector参数中包含了坐标属性！";
                    emitter.send(SseEmitter.event().name("tool_error").data(errorMsg));
                    
                    // 添加工具执行结果为用户消息
                    enhancedMessages.add(dev.langchain4j.data.message.UserMessage.from(
                        "工具 " + toolName + " 执行结果: " + errorMsg
                    ));
                    return;
                }
            }
        }
        
        // 调用MCP服务执行工具
        Map<String, Object> result = mcpService.callTool(toolName, arguments);
        
        String resultContent = "";
        if (result != null && result.containsKey("content")) {
            Object contentObj = result.get("content");
            
            // 移除ref字段
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String cleanedContent = "";
                
                if (contentObj instanceof java.util.List) {
                    java.util.List<?> contentList = (java.util.List<?>) contentObj;
                    for (Object item : contentList) {
                        try {
                            java.lang.reflect.Field textField = item.getClass().getDeclaredField("text");
                            textField.setAccessible(true);
                            Object textValue = textField.get(item);
                            if (textValue != null) {
                                String textStr = textValue.toString();
                                // 移除ref
                                String cleaned = textStr.replaceAll("\\s*\\[ref=ref_\\d+\\]", "");
                                cleanedContent += cleaned;
                            }
                        } catch (Exception e) {
                            cleanedContent += item.toString();
                        }
                    }
                } else {
                    String contentStr = contentObj.toString();
                    if (contentStr.trim().startsWith("{") || contentStr.trim().startsWith("[")) {
                        try {
                            com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(contentStr);
                            removeRefFields(rootNode);
                            cleanedContent = mapper.writeValueAsString(rootNode);
                        } catch (Exception jsonEx) {
                            cleanedContent = contentStr;
                        }
                    } else {
                        cleanedContent = contentStr;
                    }
                }
                
                resultContent = cleanedContent;
            } catch (Exception e) {
                resultContent = contentObj.toString();
            }
        }
        
        // 推送工具执行结果
        emitter.send(SseEmitter.event().name("tool_result")
                .data("工具执行结果: " + resultContent));
        
        // 添加工具执行结果到消息历史
        enhancedMessages.add(dev.langchain4j.data.message.UserMessage.from(
            "工具 " + toolName + " 执行结果: " + resultContent
        ));
    }
}