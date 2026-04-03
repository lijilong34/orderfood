package org.example.orderfoodafter.controller;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.service.AiService;
import org.example.orderfoodafter.service.McpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI-MCP控制器
 * 提供AI调用MCP工具的REST API
 * 
 * @author 周子金
 * @date 2026-03-17
 */
@RestController
@RequestMapping("/ai-mcp")
public class AiMcpController {

    @Autowired
    private AiService aiService;

    @Autowired
    private McpService mcpService;

    /**
     * AI调用MCP工具
     * @param request 请求参数（包含对话内容和工具列表）
     * @return AI的最终回复
     */
    @PostMapping("/call")
    public R callAiMcp(@RequestBody Map<String, Object> request) {
        try {
            // 从请求参数中获取对话内容
            String message = (String) request.get("message");
            if (message == null || message.isEmpty()) {
                    /**
     * R
     * 
     * @author 李吉隆
     */
                return new R().addData("success", false).addData("message", "对话内容不能为空");
            }

            // 从请求参数中获取是否包含图片
            boolean hasImage = request.containsKey("hasImage") && (boolean) request.get("hasImage");

            // 从请求参数中获取工具列表（可选）
            List<Map<String, Object>> toolList = (List<Map<String, Object>>) request.get("tools");
            List<ToolSpecification> toolSpecifications = new ArrayList<>();

            // 如果没有提供工具列表，使用简化的测试工具集（先测试AI是否能调用工具）
            if (toolList == null || toolList.isEmpty()) {
                // 暂时使用简单的硬编码工具进行测试
                toolSpecifications.add(ToolSpecification.builder()
                        .name("chrome_read_page")
                        .description("读取当前页面的可见元素，了解页面内容")
                        .parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder()
                                .build())
                        .build());
                
                toolSpecifications.add(ToolSpecification.builder()
                        .name("chrome_click_element")
                        .description("点击页面上的元素")
                        .parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder()
                                .addStringProperty("selector", "CSS选择器")
                                .required("selector")
                                .build())
                        .build());
                
                toolSpecifications.add(ToolSpecification.builder()
                        .name("chrome_navigate")
                        .description("导航到指定的URL")
                        .parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder()
                                .addStringProperty("url", "要导航的URL")
                                .required("url")
                                .build())
                        .build());
                
                System.out.println("使用简化的测试工具集（3个工具）");
                
                /* 原代码（暂时注释掉）
                List<Map<String, Object>> mcpTools = mcpService.listTools();
                for (Map<String, Object> tool : mcpTools) {
                    String toolName = (String) tool.get("name");
                    String description = tool.get("description") != null ? (String) tool.get("description") : "";
                    String chineseDescription = getChineseToolDescription(toolName, description);
                    
                    // 提取inputSchema
                    Object inputSchema = tool.get("inputSchema");
                    
                    // 构建工具规范，使用JsonObjectSchema作为参数定义
                    var builder = ToolSpecification.builder()
                            .name(toolName)
                            .description(chineseDescription);
                    
                    // 添加参数定义（使用JsonObjectSchema）
                    try {
                        builder.parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder().build());
                        System.out.println("工具 " + toolName + " 参数定义添加成功（空Schema）");
                    } catch (Exception e) {
                        System.err.println("工具 " + toolName + " 参数定义添加失败: " + e.getMessage());
                    }
                    
                    toolSpecifications.add(builder.build());
                    
                    System.out.println("工具 " + toolName + " 添加完成");
                }
                */
            } else {
                // 如果提供了工具列表，使用指定的工具
                for (Map<String, Object> tool : toolList) {
                    String toolName = (String) tool.get("name");
                    String description = tool.get("description") != null ? (String) tool.get("description") : "";

                    // 添加中文描述，确保AI能够理解和使用MCP工具
                    String chineseDescription = getChineseToolDescription(toolName, description);
                    
                    // 提取inputSchema
                    Object inputSchema = tool.get("inputSchema");

                    var builder = ToolSpecification.builder()
                            .name(toolName)
                            .description(chineseDescription);
                    
                    // 添加参数定义（使用inputSchema）
                    try {
                        if (inputSchema instanceof Map) {
                            builder.parameters(convertInputSchemaToJsonObjectSchema((Map<String, Object>) inputSchema));
                            System.out.println("工具 " + toolName + " 参数定义添加成功（使用inputSchema）");
                        } else {
                            builder.parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder().build());
                            System.out.println("工具 " + toolName + " 参数定义添加成功（空Schema）");
                        }
                    } catch (Exception e) {
                        System.err.println("工具 " + toolName + " 参数定义添加失败: " + e.getMessage());
                        // 如果转换失败，使用空的对象
                        try {
                            builder.parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder().build());
                        } catch (Exception ex) {
                            System.err.println("  添加空schema也失败: " + ex.getMessage());
                        }
                    }

                    toolSpecifications.add(builder.build());
                }
            }

            // 构建对话消息
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(UserMessage.from(message));

            // 执行工具调用循环
            String result = aiService.executeToolLoop(messages, toolSpecifications, mcpService, 3);

            // 返回成功结果
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("success", true).addData("result", result);
        } catch (Exception e) {
            // 捕获异常，返回失败结果
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("success", false).addData("message", "调用AI-MCP工具失败: " + e.getMessage());
        }
    }

    /**
     * 获取AI-MCP工具列表
     * @return 工具列表
     */
    @GetMapping("/tools")
    public R getAiMcpTools() {
        try {
            // 获取所有可用的MCP工具
            List<Map<String, Object>> tools = mcpService.listTools();
            
            // 返回成功结果，包含工具列表
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("success", true).addData("tools", tools);
        } catch (Exception e) {
            // 捕获异常，返回失败结果
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("success", false).addData("message", "获取工具列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取MCP工具的中文描述
     * @param toolName 工具名称
     * @param originalDescription 原始描述
     * @return 中文描述
     */
    private String getChineseToolDescription(String toolName, String originalDescription) {
        // 为所有MCP工具提供详细的中文描述
        switch (toolName) {
            case "get_windows_and_tabs":
                return "获取所有打开的浏览器窗口和标签页信息，包括窗口ID、标签页ID、URL、标题等。无参数。例如：{}";
            case "chrome_navigate":
                return "打开指定的网页或导航到URL。参数：url（必需，网页地址），newWindow（可选，是否在新窗口打开），width/height（可选，新窗口尺寸）。例如：{\"url\": \"https://www.baidu.com\"} 或 {\"url\": \"https://www.missfresh.com\", \"newWindow\": true}";
            case "chrome_screenshot":
                return "截取当前网页的截图。参数：storeBase64（推荐true，返回base64编码），savePng（推荐false，不保存文件），fullPage（可选，是否截取全屏）。例如：{\"savePng\": false, \"storeBase64\": true}";
            case "chrome_read_page":
                return "【函数调用】读取网页内容，获取页面上可见的元素和文本。\n\n" +
                       "这是一个可以直接调用的工具函数，不是文本描述！\n" +
                       "参数：\n" +
                       "- filter（字符串，可选）：'interactive'只获取可交互元素\n" +
                       "- depth（整数，可选）：最大深度\n" +
                       "调用方式：{\"filter\": \"interactive\"} 或 {}\n" +
                       "重要：必须直接调用此工具，不要在文本中描述它！";
            case "chrome_computer":
                return "使用鼠标和键盘与浏览器交互，支持点击、滚动、输入、截图等操作。参数：action（必需，操作类型：left_click/right_click/scroll/type/key/screenshot等），coordinates（可选，坐标），text（可选，输入文本），scrollDirection（可选，滚动方向）。例如：{\"action\": \"left_click\", \"coordinates\": {\"x\": 100, \"y\": 200}}";
            case "chrome_click_element":
                return "点击网页上的元素。参数：selector（CSS选择器或XPath），double（可选，是否双击），button（可选，鼠标按钮）。【警告】绝对禁止在selector中使用ref！ref是内部引用ID，不是HTML属性，不能作为CSS选择器使用！错误示例：{\"selector\": \"button[ref='ref_41']\"}（会失败）。正确示例：{\"selector\": \"#button-id\"} 或 {\"selector\": \"button.close\"} 或 {\"selector\": \"//button[contains(@class, 'close')]\", \"selectorType\": \"xpath\"}。如果页面元素有ref，请根据元素的其他属性（如class、id、text）来构建selector。";
            case "chrome_fill_or_select":
                return "在网页输入框中输入文本或选择下拉框选项。参数：selector（CSS选择器），value（必需，输入值或选项值）。【警告】绝对禁止在selector中使用ref！ref是内部引用ID，不是HTML属性，不能作为CSS选择器使用！错误示例：{\"selector\": \"input[ref='ref_47']\"}（会失败）。正确示例：{\"selector\": \"#el-id-1166-4\"} 或 {\"selector\": \"input[type='text']\"} 或 {\"selector\": \".input-box\"}。请根据元素的实际HTML属性（如id、class、type）来构建selector。";
            case "chrome_keyboard":
                return "模拟键盘输入，支持单个按键、组合键和文本输入。参数：keys（必需，按键或文本）。例如：{\"keys\": \"Enter\"} 或 {\"keys\": \"Ctrl+C\"} 或 {\"keys\": \"你好\"}";
            case "chrome_close_tabs":
                return "关闭浏览器标签页。参数：tabIds（可选，标签页ID数组），url（可选，按URL关闭）。例如：{\"tabIds\": [12345]}";
            case "chrome_switch_tab":
                return "切换到指定的浏览器标签页。参数：tabId（必需，标签页ID），windowId（可选，窗口ID）。例如：{\"tabId\": 12345}";
            case "chrome_get_web_content":
                return "获取网页的文本或HTML内容。参数：textContent（可选，获取文本，默认true），htmlContent（可选，获取HTML），selector（可选，CSS选择器）。例如：{}";
            case "chrome_network_request":
                return "从浏览器发送网络请求，带有浏览器的cookie和上下文。参数：url（必需），method（可选，默认GET），headers（可选），body（可选）。例如：{\"url\": \"https://api.example.com/data\", \"method\": \"POST\"}";
            case "chrome_network_capture":
                return "捕获网络请求，用于监控和分析网络流量。参数：action（必需，start/stop），needResponseBody（可选，是否捕获响应体）。例如：{\"action\": \"start\"}";
            case "chrome_handle_download":
                return "等待浏览器下载完成并返回下载文件信息。参数：filenameContains（可选，文件名过滤），timeoutMs（可选，超时时间）。例如：{}";
            case "chrome_history":
                return "检索和搜索浏览器历史记录。参数：text（可选，搜索文本），startTime/endTime（可选，时间范围）。例如：{\"text\": \"奶茶\", \"startTime\": \"1 day ago\"}";
            case "chrome_bookmark_search":
                return "搜索浏览器书签。参数：query（可选，搜索关键词），folderPath（可选，文件夹路径）。例如：{\"query\": \"外卖\"}";
            case "chrome_bookmark_add":
                return "添加浏览器书签。参数：url（可选，默认当前页），title（可选，书签标题）。例如：{\"url\": \"https://www.missfresh.com\", \"title\": \"每日优鲜\"}";
            case "chrome_bookmark_delete":
                return "删除浏览器书签。参数：bookmarkId（可选，书签ID），url（可选，按URL删除）。例如：{\"url\": \"https://www.missfresh.com\"}";
            case "chrome_javascript":
                return "在浏览器中执行JavaScript代码并返回结果。参数：code（必需，JavaScript代码）。例如：{\"code\": \"document.title\"}";
            case "chrome_upload_file":
                return "上传文件到网页表单。参数：selector（必需，文件输入框选择器），filePath（可选，本地文件路径）。例如：{\"selector\": \"input[type='file']\", \"filePath\": \"C:\\Users\\user\\file.jpg\"}";
            case "chrome_handle_dialog":
                return "处理JavaScript弹窗（alert/confirm/prompt）。参数：action（必需，accept/dismiss），promptText（可选，提示框输入的文本）。例如：{\"action\": \"accept\"}";
            case "chrome_request_element_selection":
                return "请求用户手动选择页面上的元素，用于无法自动定位元素的情况。参数：requests（必需，选择请求列表）。例如：{\"requests\": [{\"name\": \"登录按钮\"}]}";
            case "chrome_console":
                return "捕获浏览器的控制台输出，用于调试。参数：mode（可选，snapshot/buffer），onlyErrors（可选，只获取错误）。例如：{\"mode\": \"snapshot\"}";
            case "chrome_gif_recorder":
                return "录制浏览器操作为GIF动画。参数：action（必需，start/stop/export），fps（可选，帧率）。例如：{\"action\": \"start\", \"fps\": 5}";
            case "performance_start_trace":
                return "开始性能追踪，用于分析网页性能。参数：reload（可选，是否刷新页面），autoStop（可选，是否自动停止）。例如：{\"reload\": true}";
            case "performance_stop_trace":
                return "停止性能追踪并保存结果。参数：saveToDownloads（可选，是否保存到下载），filenamePrefix（可选，文件名前缀）。例如：{}";
            case "performance_analyze_insight":
                return "分析性能追踪数据，提供性能洞察。参数：insightName（可选，洞察名称）。例如：{}";
            default:
                // 如果没有中文描述，使用原始描述
                return originalDescription;
        }
    }

    // SSE端点，实时推送MCP工具执行过程
    private final ExecutorService sseExecutorService = Executors.newCachedThreadPool();
/**
 * streamAiMcp方法
 *
 * @author 李吉隆
 */

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAiMcp(@RequestParam String message,
                                  @RequestParam(required = false) String imgone) {
            /**
     * SseEmitter
     *
     * @author 李吉隆
     */
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        sseExecutorService.execute(() -> {
            try {
                System.out.println("=== streamAiMcp 接收到请求 ===");
                System.out.println("message: " + message);
                System.out.println("imgone: " + imgone);

                // 获取图片描述（使用智谱AI）
                String desctext = "用户未上传图片";
                if (imgone != null && !imgone.isEmpty()) {
                    try {
                        System.out.println("开始获取图片描述，图片名称: " + imgone);
                        desctext = aiService.getImageDescription(imgone);
                        System.out.println("图片描述: " + desctext);
                    } catch (Exception e) {
                        System.err.println("获取图片描述失败: " + e.getMessage());
                        desctext = "图片描述获取失败";
                    }
                } else {
                    System.out.println("imgone为空或null，跳过图片描述获取");
                }

                // 如果有图片描述,将图片描述添加到消息前面
                String fullMessage = message;
                if (!"用户未上传图片".equals(desctext) && !"图片描述获取失败".equals(desctext)) {
                    fullMessage = "用户上传的图片描述: " + desctext + "\n\n用户问题: " + message;
                }
                System.out.println("最终发送给AI的完整消息: " + fullMessage);

                // 构建工具列表
                List<ToolSpecification> toolSpecifications = new ArrayList<>();
                List<Map<String, Object>> mcpTools = mcpService.listTools();
                
                for (Map<String, Object> tool : mcpTools) {
                    String toolName = (String) tool.get("name");
                    String description = tool.get("description") != null ? (String) tool.get("description") : "";
                    String chineseDescription = getChineseToolDescription(toolName, description);
                    
                    // 添加参数定义（使用MCP返回的inputSchema）
                    var builder = ToolSpecification.builder()
                            .name(toolName)
                            .description(chineseDescription);
                    
                    try {
                        // 使用MCP返回的inputSchema
                        if (tool.containsKey("inputSchema")) {
                            Map<String, Object> inputSchema = (Map<String, Object>) tool.get("inputSchema");
                            builder.parameters(convertInputSchemaToJsonObjectSchema(inputSchema));
                            System.out.println("工具 " + toolName + " 参数定义添加成功（使用inputSchema）");
                        } else {
                            // 如果没有inputSchema，使用空的对象
                            builder.parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder().build());
                            System.out.println("工具 " + toolName + " 参数定义添加成功（使用空schema）");
                        }
                    } catch (Exception e) {
                        System.err.println("工具 " + toolName + " 参数定义添加失败: " + e.getMessage());
                        // 如果转换失败，使用空的对象
                        try {
                            builder.parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder().build());
                        } catch (Exception ex) {
                            System.err.println("  添加空schema也失败: " + ex.getMessage());
                        }
                    }
                    
                    toolSpecifications.add(builder.build());
                    
                    System.out.println("工具 " + toolName + " 添加完成");
                }

                // 使用langchain4j调用工具，实时推送执行过程
                aiService.executeToolLoopWithSSE(emitter, fullMessage, toolSpecifications, mcpService, 3);

                // 发送完成事件前检查emitter状态
                try {
                    emitter.send(SseEmitter.event().name("done").data("完成"));
                    emitter.complete();
                } catch (Exception e) {
                    // 如果发送失败，可能是emitter已经完成，直接忽略
                    System.out.println("发送完成事件失败，emitter可能已经完成: " + e.getMessage());
                    try {
                        emitter.complete();
                    } catch (Exception ex) {
                        // ignore
                    }
                }
            } catch (Exception e) {
                // 检查是否是客户端连接中断
                String errorMsg = e.getMessage();
                if (errorMsg != null && (errorMsg.contains("已建立的连接") || 
                    errorMsg.contains("Broken pipe") || 
                    errorMsg.contains("Connection reset") ||
                    errorMsg.contains("ResponseBodyEmitter has already completed"))) {
                    // 客户端关闭连接或emitter已完成，直接完成emitter，不尝试发送错误信息
                    System.out.println("客户端关闭SSE连接或emitter已完成，正常结束");
                    try {
                        emitter.complete();
                    } catch (Exception ex) {
                        // ignore
                    }
                } else {
                    // 其他异常，尝试发送错误信息
                    try {
                        emitter.send(SseEmitter.event().name("error").data("错误: " + e.getMessage()));
                        emitter.completeWithError(e);
                    } catch (Exception ex) {
                        // 如果发送失败，直接完成
                        System.err.println("发送错误信息失败: " + ex.getMessage());
                        try {
                            emitter.completeWithError(ex);
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
        });

        return emitter;
    }
    
    /**
     * 将MCP的inputSchema转换为LangChain4j的JsonObjectSchema
     */
    private dev.langchain4j.model.chat.request.json.JsonObjectSchema convertInputSchemaToJsonObjectSchema(Map<String, Object> inputSchema) {
        try {
            System.out.println("转换inputSchema: " + inputSchema);
            
            // 检查是否有properties
            if (inputSchema.containsKey("properties")) {
                System.out.println("  - 包含properties字段");
            }
            
            if (inputSchema.containsKey("required")) {
                System.out.println("  - 包含required字段: " + inputSchema.get("required"));
            }
            
            // 当前版本使用空的对象schema
            // TODO: 后续可以增强为完整的schema转换
            return dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder().build();
        } catch (Exception e) {
            System.err.println("转换inputSchema失败: " + e.getMessage());
            // 如果转换失败，返回空的对象
            return dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder().build();
        }
    }
}
