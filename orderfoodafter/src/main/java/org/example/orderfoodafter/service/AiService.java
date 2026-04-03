package org.example.orderfoodafter.service;

import ai.z.openapi.service.model.*;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * AI服务接口
 * 提供统一的AI调用接口，根据是否有图片自动选择使用智谱AI或langchain4j
 *
 * @author iFlow
 * @date 2025-03-15
 */
public interface AiService {

    /**
     * 调用AI对话（非流式）
     *
     * @param messages 对话消息列表
     * @param hasImage 是否包含图片（用户上传图片或MCP截图）
     * @param tools 工具列表（可选，用于MCP工具调用）
     * @return AI回复
     */
    String chat(List<ChatMessage> messages, boolean hasImage, List<ChatTool> tools) throws Exception;

    /**
     * 调用AI对话（非流式）- langchain4j专用
     *
     * @param messages 对话消息列表
     * @return AI回复
     */
    String chatWithLangChain4j(List<UserMessage> messages) throws Exception;

    /**
     * 调用AI对话（流式）- langchain4j专用
     *
     * @param messages 对话消息列表
     * @param handler 流式响应处理器
     */
    void chatWithLangChain4jStream(List<UserMessage> messages, dev.langchain4j.model.chat.response.StreamingChatResponseHandler handler) throws Exception;

    /**
     * 调用AI对话（非流式）- langchain4j专用，支持工具
     *
     * @param messages 对话消息列表
     * @param tools 工具列表
     * @return AI回复
     */
    String chatWithLangChain4jWithTools(List<UserMessage> messages, List<ToolSpecification> tools) throws Exception;

    /**
     * 执行完整的工具调用循环
     * 接收用户输入→拼接上下文→调用大模型→判断是否要工具调用
     * 如果有工具调用，执行工具并返回结果给大模型开始新的一轮
     * 循环直到大模型无需工具调用
     * 工具调用失败时返回结果给大模型让其用其他方案
     * 工具调用失败五次返回结果给用户
     *
     * @param messages 对话消息列表
     * @param tools 工具规范列表
     * @param mcpService MCP服务
     * @param maxRetries 最大重试次数
     * @return AI最终回复
     */
    String executeToolLoop(List<dev.langchain4j.data.message.ChatMessage> messages, 
                           List<ToolSpecification> tools,
                           org.example.orderfoodafter.service.McpService mcpService,
                           int maxRetries) throws Exception;

    /**
     * 调用AI对话（非流式）- 智谱AI专用
     *
     * @param messages 对话消息列表
     * @param tools 工具列表（可选）
     * @return AI回复
     */
    String chatWithZhipuAi(List<ChatMessage> messages, List<ChatTool> tools) throws Exception;

    /**
     * 调用AI对话（非流式）- 智谱AI专用，返回完整响应
     *
     * @param messages 对话消息列表
     * @param tools 工具列表（可选）
     * @return AI完整响应（包含工具调用信息）
     */
    ChatCompletionResponse chatWithZhipuAiWithResponse(List<ChatMessage> messages, List<ChatTool> tools) throws Exception;

    /**
     * 获取图片描述
     *
     * @param imgname 图片名称
     * @return 图片描述
     */
    String getImageDescription(String imgname) throws Exception;

    /**
     * 执行工具调用循环，并通过SSE实时推送执行过程
     *
     * @param emitter SSE发射器
     * @param message 用户消息
     * @param tools 工具规范列表
     * @param mcpService MCP服务
     * @param maxRetries 最大重试次数
     */
    void executeToolLoopWithSSE(SseEmitter emitter, String message,
                                 List<ToolSpecification> tools,
                                 org.example.orderfoodafter.service.McpService mcpService,
                                 int maxRetries) throws Exception;
}