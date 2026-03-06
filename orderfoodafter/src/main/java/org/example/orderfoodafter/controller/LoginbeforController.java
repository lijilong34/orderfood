package org.example.orderfoodafter.controller;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.DefaulteProperties;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.common.WhereEntity;
import org.example.orderfoodafter.entity.Product;
import org.example.orderfoodafter.entity.Shop;
import org.example.orderfoodafter.service.ProductService;
import org.example.orderfoodafter.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicLong;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录前的控制类
 * 时间:2025/11/27
 * 作者：李吉隆
 */
@RestController
@RequestMapping("/Shopforbefor")
public class LoginbeforController extends BaseController<Shop, ShopService> {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ProductService productService;
    @Autowired
    private org.example.orderfoodafter.mapper.EvaluationMapper evaluationMapper;
    @Autowired
    private org.example.orderfoodafter.service.McpService mcpService;

    public LoginbeforController(ShopService shopService) {
        super(shopService);
        this.shopService = shopService;
    }

    /**
     * 查询销量前十的商店
     *
     * @return
     */
    @GetMapping("/selectshopbytop10")
    public R selectshopbytop10() {
        List<Shop> shopList = shopService.selectShopBytop();
        return new R().addData("shopList", shopList);
    }



    /**
     * 多条件查询当前订单
     * @param id
     * @param cid
     * @param nickname
     * @param phone
     * @return
     */
    @PostMapping("/selectshopbylist")
    public R selectshopbyid(@RequestParam("id") int id,@RequestParam("cid") int cid,@RequestParam("nickname") String nickname,@RequestParam("phone") String phone,@RequestParam("page") String page) {
        int orderpage=0;
        if (page==null){
            orderpage=1;
        }else {
            orderpage=Integer.parseInt(page);
        }
        Page<Object> obj= PageHelper.startPage(orderpage, 6);
        List<Shop> shopList=shopService.selectShopBylist(id,cid, nickname,phone);
        PageInfo pageInfo = new PageInfo<>(shopList);
        return new R().addData("shopList", shopList).addData("obj",pageInfo);
    }

    /**
     * 描述:查询整个平台前五的菜品
     * 作者：赵康乐
     * 日期:2025/12/6
     */
    @GetMapping("/selectproducttop5")
    public R selectproducttop5() {
        List<Product> productList = productService.selectproducttop5();
        return new R().addData("productList", productList);
    }

    private static final ZhipuAiClient client = ZhipuAiClient.builder()
            .apiKey("f298b63fa87f451889ea75c33f3ef58e.sWVWtdr2Qe4UKdtd")
            .build();


    // 重试次数
    private static final int MAX_RETRIES = 5;
    // 初始重试间隔（毫秒）
    private static final long BASE_RETRY_INTERVAL = 2000;

    // 令牌桶算法参数
    private static final long REFILL_RATE = 1; // 每秒生成1个令牌
    private static final long BUCKET_CAPACITY = 5; // 桶容量
    private static final AtomicLong availableTokens = new AtomicLong(BUCKET_CAPACITY); // 当前可用令牌数
    private static final AtomicLong lastRefillTime = new AtomicLong(System.currentTimeMillis()); // 上次令牌补充时间

    // 简单的并发限流，使用AtomicInteger记录当前请求数
    private static final AtomicInteger currentRequests = new AtomicInteger(0);
    private static final int MAX_CONCURRENT_REQUESTS = 3;

    // 请求缓存，使用ConcurrentHashMap存储用户请求和对应的AI响应
    // 键：用户输入文本，值：AI响应结果
    // 设置缓存过期时间为30分钟
    private static final ConcurrentHashMap<String, CacheEntry> requestCache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_TIME = 30 * 60 * 1000; // 30分钟

    // 缓存条目类
    private static class CacheEntry {
        private final String response;
        private final long timestamp;

        public CacheEntry(String response) {
            this.response = response;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME;
        }

        public String getResponse() {
            return response;
        }
    }

    // 令牌桶算法：补充令牌
    private void refillTokens() {
        long now = System.currentTimeMillis();
        long timeElapsed = now - lastRefillTime.get();

        if (timeElapsed > 0) {
            // 计算应该补充的令牌数
            long tokensToAdd = (timeElapsed / 1000) * REFILL_RATE;

            if (tokensToAdd > 0) {
                // 更新令牌数和补充时间
                long currentTokens = availableTokens.get();
                long newTokens = Math.min(currentTokens + tokensToAdd, BUCKET_CAPACITY);

                if (availableTokens.compareAndSet(currentTokens, newTokens)) {
                    lastRefillTime.set(now);
                }
            }
        }
    }

    // 令牌桶算法：尝试获取令牌
    private boolean tryAcquireToken() {
        refillTokens();

        while (true) {
            long currentTokens = availableTokens.get();
            if (currentTokens < 1) {
                return false;
            }

            if (availableTokens.compareAndSet(currentTokens, currentTokens - 1)) {
                return true;
            }
        }
    }

    // 令牌桶算法：释放令牌
    private void releaseToken() {
        long currentTokens = availableTokens.get();
        if (currentTokens < BUCKET_CAPACITY) {
            availableTokens.compareAndSet(currentTokens, currentTokens + 1);
        }
    }

    // 存储对话历史，用于多轮工具调用
    private static final ConcurrentHashMap<String, List<ChatMessage>> conversationHistory = new ConcurrentHashMap<>();

    @PostMapping("/takeai")
    public R takeai(@RequestBody Map<String, String> text1) throws IOException {
        // 移除JSON字符串的引号（如果前端发送的是带引号的字符串）
        String text = text1.get("message");
        String conversationId = text1.get("conversationId"); // 对话ID，用于区分不同会话
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = "default";
        }

        System.out.println("收到用户输入: " + text + ", 对话ID: " + conversationId);

        String imgname=text1.get("imgone");
        String desctext=null;
        if (imgname!=null){
            desctext=getdesc(imgname);
        }else {
            desctext="用户未上传图片";
        }
        System.out.println("图片描述: " + desctext);

        // 获取工具结果
        String toolResults = text1.get("toolResults");

        // 检查message参数是否为空（但如果有toolResults，则允许message为空）
        if ((text == null || text.trim().isEmpty()) && (toolResults == null || toolResults.isEmpty())) {
            R errorResult = new R();
            errorResult.setCode(400);
            errorResult.setMessage("请输入有效的对话内容");
            return errorResult;
        }

        // 获取或创建对话历史
        List<ChatMessage> messages = conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>());

        // 如果有工具调用结果，将其添加到对话历史
        if (toolResults != null && !toolResults.isEmpty()) {
            System.out.println("收到工具执行结果: " + toolResults);

            // 检查是否包含截图
            if (toolResults.contains("screenshot")) {
                System.out.println("检测到截图，创建图片消息");
                // 创建包含截图的消息
                String screenshotUrl = extractScreenshotUrl(toolResults);
                if (screenshotUrl != null && !screenshotUrl.isEmpty()) {
                    System.out.println("截图URL长度: " + screenshotUrl.length());
                    ChatMessage imageMessage = ChatMessage.builder()
                            .role("user")
                            .content(Arrays.asList(
                                    MessageContent.builder()
                                            .type("text")
                                            .text("这是当前页面的截图，请根据截图内容回答用户的问题或执行操作。")
                                            .build(),
                                    MessageContent.builder()
                                            .type("image_url")
                                            .imageUrl(ImageUrl.builder()
                                                    .url(screenshotUrl)
                                                    .build())
                                            .build()))
                            .build();
                    messages.add(imageMessage);
                } else {
                    System.out.println("截图URL为空，使用文本消息");
                    messages.add(ChatMessage.builder()
                            .role("tool")
                            .content(toolResults)
                            .build());
                }
            } else {
                // 不包含截图，作为普通文本处理
                messages.add(ChatMessage.builder()
                        .role("tool")
                        .content(toolResults)
                        .build());
            }
        } else if (text != null && !text.isEmpty()) {
            // 只有当message不为空时才添加用户消息到对话历史
            messages.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(text)
                    .build());
        }

        // 限制对话历史长度，避免过长
        if (messages.size() > 20) {
            messages = messages.subList(messages.size() - 20, messages.size());
        }

        // 【重要】当有工具执行结果时，跳过缓存检查，继续对话
        if (toolResults == null || toolResults.isEmpty()) {
            // 先检查缓存，减少重复AI调用
            CacheEntry cachedEntry = requestCache.get(text);
            if (cachedEntry != null) {
                if (!cachedEntry.isExpired()) {
                    System.out.println("从缓存中获取AI响应");
                    R result = new R().addData("aitake", cachedEntry.getResponse());
                    System.out.println("返回缓存结果给前端");
                    return result;
                } else {
                    // �存过期，移除
                    requestCache.remove(text);
                    System.out.println("缓存已过期，移除旧缓存");
                }
            }
        } else {
            System.out.println("有工具执行结果，跳过缓存检查，继续对话");
        }

        // 使用令牌桶算法进行速率限制
        if (!tryAcquireToken()) {
            R errorResult = new R();
            errorResult.setCode(503);
            errorResult.setMessage("当前AI请求过多，请稍后再试");
            return errorResult;
        }

        // 简单的并发限流，防止系统过载
        if (currentRequests.get() >= MAX_CONCURRENT_REQUESTS) {
            R errorResult = new R();
            errorResult.setCode(503);
            errorResult.setMessage("当前AI请求过多，请稍后再试");
            return errorResult;
        }

        // MCP Chrome 浏览器工具定义
        List<ChatTool> aiTools = new ArrayList<>();

        // 导航工具
        aiTools.add(ChatTool.builder()
                .type("function")
                .function(ChatFunction.builder()
                        .name("chrome_navigate")
                        .description("Navigate to a URL, refresh the current tab, or navigate browser history")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(Map.of(
                                        "url", ChatFunctionParameterProperty.builder().type("string").description("URL to navigate to").build()
                                ))
                                .required(Arrays.asList("url"))
                                .build())
                        .build())
                .build());

        // 截图工具
        aiTools.add(ChatTool.builder()
                .type("function")
                .function(ChatFunction.builder()
                        .name("chrome_screenshot")
                        .description("Take a screenshot of the current page")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(Map.of())
                                .required(Arrays.asList())
                                .build())
                        .build())
                .build());

        // 读取页面工具
        aiTools.add(ChatTool.builder()
                .type("function")
                .function(ChatFunction.builder()
                        .name("chrome_read_page")
                        .description("Get an accessibility tree representation of visible elements on the page")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(Map.of())
                                .required(Arrays.asList())
                                .build())
                        .build())
                .build());

        // 点击元素工具
        aiTools.add(ChatTool.builder()
                .type("function")
                .function(ChatFunction.builder()
                        .name("chrome_click_element")
                        .description("Click on an element in a web page")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(Map.of(
                                        "selector", ChatFunctionParameterProperty.builder().type("string").description("CSS selector or XPath for the element").build()
                                ))
                                .required(Arrays.asList("selector"))
                                .build())
                        .build())
                .build());

        // 填写表单工具
        aiTools.add(ChatTool.builder()
                .type("function")
                .function(ChatFunction.builder()
                        .name("chrome_fill_or_select")
                        .description("Fill or select a form element on a web page")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(Map.of(
                                        "selector", ChatFunctionParameterProperty.builder().type("string").description("CSS selector or XPath").build(),
                                        "value", ChatFunctionParameterProperty.builder().type("string").description("Value to fill").build()
                                ))
                                .required(Arrays.asList("selector", "value"))
                                .build())
                        .build())
                .build());

        System.out.println("已添加 " + aiTools.size() + " 个 Chrome 工具");

        // 创建聊天完成请求，使用对话历史
        List<ChatMessage> messageList = new ArrayList<>();

        // 添加系统提示
        messageList.add(ChatMessage.builder()
                .role("system")
                .content("浏览器助手。图片:"+desctext+"。规则:1.只在本系统操作 2.持续执行直到满足需求 3.用工具:截图/读页面/点击/填写")
                .build());

        // 添加对话历史
        messageList.addAll(messages);

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model("glm-4.6v-flash")
                .messages(messageList)
                .tools(aiTools)
                .build();

        System.out.println("开始调用AI API...");
        int retryCount = 0;
        Random random = new Random();

        // 添加重试机制
        while (retryCount < MAX_RETRIES) {
            try {
                // 增加当前请求计数
                currentRequests.incrementAndGet();

                // 发送请求（同步调用，会等待AI完全响应）
                ChatCompletionResponse response = client.chat().createChatCompletion(request);
                System.out.println("AI API调用完成");

                // 获取回复
                if (response.isSuccess()) {
                    ChatMessage message = response.getData().getChoices().get(0).getMessage();
                    Object reply = message.getContent();
                    List<ToolCalls> toolCallsList = message.getToolCalls();

                    // 如果AI调用了工具，自动执行 MCP 工具并将结果返回给 AI 继续对话
                    if (toolCallsList != null && !toolCallsList.isEmpty()) {
                        System.out.println("AI调用了工具，工具数量: " + toolCallsList.size());

                        // 流式循环调用：持续执行工具直到 AI 给出最终文本回复
                        int maxIterations = 10; // 最大迭代次数，防止无限循环
                        int iteration = 0;
                        
                        while (iteration < maxIterations) {
                            iteration++;
                            System.out.println("=== 流式调用迭代 " + iteration + " ===");
                            
                            // 将AI的回复（包含工具调用）添加到对话历史
                            messages.add(message);

                            // 执行所有工具调用
                            for (ToolCalls toolCalls : toolCallsList) {
                                ChatFunctionCall functionCall = toolCalls.getFunction();
                                if (functionCall != null) {
                                    String toolName = functionCall.getName();
                                    Object argsObj = functionCall.getArguments();
                                    System.out.println("执行 MCP 工具: " + toolName + ", 参数: " + argsObj);

                                    try {
                                        // 解析参数
                                        Object argsData = functionCall.getArguments();
                                        Map<String, Object> arguments;
                                        if (argsData instanceof String) {
                                            arguments = parseArguments((String) argsData);
                                        } else if (argsData instanceof Map) {
                                            arguments = (Map<String, Object>) argsData;
                                        } else {
                                            arguments = new HashMap<>();
                                        }

                                        // 调用 MCP 工具
                                        Map<String, Object> mcpResult = mcpService.callTool(toolName, arguments);
                                        System.out.println("MCP 工具执行结果: " + mcpResult);

                                        // 将工具执行结果添加到对话历史
                                        String resultContent = formatMcpResult(mcpResult);
                                        messages.add(ChatMessage.builder()
                                                .role("tool")
                                                .content(resultContent)
                                                .build());

                                    } catch (Exception e) {
                                        System.err.println("执行 MCP 工具失败: " + e.getMessage());
                                        e.printStackTrace();
                                        // 将错误信息添加到对话历史，让AI知道工具失败了
                                        messages.add(ChatMessage.builder()
                                                .role("tool")
                                                .content("工具执行失败: " + e.getMessage() + "。请尝试其他方法或告诉用户无法完成该操作。")
                                                .build());
                                    }
                                }
                            }

                            // 限制对话历史长度
                            if (messages.size() > 20) {
                                messages = messages.subList(messages.size() - 20, messages.size());
                            }

                            // 将更新后的对话历史保存
                            conversationHistory.put(conversationId, messages);

                            // 重新调用 AI，让 AI 根据工具执行结果继续对话
                            System.out.println("根据工具执行结果，继续调用 AI...");
                            
                            // 更新 messageList，包含最新的对话历史
                            messageList = new ArrayList<>();
                            messageList.add(ChatMessage.builder()
                                    .role("system")
                                    .content("浏览器助手。图片:"+desctext+"。规则:1.只在本系统操作 2.持续执行直到满足需求 3.用工具:截图/读页面/点击/填写")
                                    .build());
                            messageList.addAll(messages);

                            // 检查是否有工具执行失败，如果有则移除 tools 参数，强制 AI 给出文本回复
                            boolean hasToolError = messages.stream()
                                .anyMatch(msg -> msg.getRole().equals("tool") && 
                                    msg.getContent() instanceof String && 
                                    ((String)msg.getContent()).contains("工具执行失败"));
                            
                            ChatCompletionCreateParams followUpRequest;
                            
                            // 如果有工具执行失败，不提供 tools 参数，强制 AI 给出文本回复
                            if (!hasToolError) {
                                followUpRequest = ChatCompletionCreateParams.builder()
                                    .model("glm-4.6v-flash")
                                    .messages(messageList)
                                    .tools(aiTools)
                                    .build();
                            } else {
                                System.out.println("检测到工具执行失败，移除 tools 参数，强制 AI 给出文本回复");
                                followUpRequest = ChatCompletionCreateParams.builder()
                                    .model("glm-4.6v-flash")
                                    .messages(messageList)
                                    .build();
                            }

                            ChatCompletionResponse followUpResponse = client.chat().createChatCompletion(followUpRequest);
                            if (followUpResponse.isSuccess()) {
                                ChatMessage followUpMessage = followUpResponse.getData().getChoices().get(0).getMessage();
                                Object followUpReply = followUpMessage.getContent();
                                toolCallsList = followUpMessage.getToolCalls();
                                
                                // 检查 AI 是否又调用了工具
                                if (toolCallsList != null && !toolCallsList.isEmpty()) {
                                    System.out.println("AI 继续调用了工具，进入下一轮迭代");
                                    message = followUpMessage; // 更新 message 为新的 AI 回复
                                    continue; // 继续循环
                                } else {
                                    // AI 给出了最终文本回复，结束循环
                                    System.out.println("AI 给出了最终回复，结束流式调用");
                                    
                                    // 将 AI 的最终回复添加到对话历史
                                    messages.add(followUpMessage);
                                    
                                    // 缓存结果
                                    if (followUpReply instanceof String) {
                                        requestCache.put(text, new CacheEntry((String) followUpReply));
                                    }

                                    R result = new R()
                                            .addData("aitake", followUpReply)
                                            .addData("conversationId", conversationId)
                                            .addData("iterations", iteration);
                                    System.out.println("返回 AI 最终结果，共迭代 " + iteration + " 次");
                                    return result;
                                }
                            } else {
                                R errorResult = new R();
                                errorResult.setCode(500);
                                errorResult.setMessage("AI 继续对话失败: " + followUpResponse.getMsg());
                                return errorResult;
                            }
                        }
                        
                        // 达到最大迭代次数，返回错误
                        R errorResult = new R();
                        errorResult.setCode(500);
                        errorResult.setMessage("AI 操作超过最大迭代次数，请稍后再试");
                        return errorResult;
                    }

                    // 普通文本回复
                    if (reply instanceof String) {
                        System.out.println("AI回复内容长度: " + ((String)reply).length() + " 字符");
                        // 将AI响应存储到缓存
                        requestCache.put(text, new CacheEntry((String)reply));
                        System.out.println("AI响应已缓存");

                        // 将AI的回复添加到对话历史
                        messages.add(message);
                    }
                    System.out.println("AI回复: " + reply);

                    System.out.println("准备返回结果给前端");
                    R result = new R()
                            .addData("aitake", reply)
                            .addData("conversationId", conversationId);
                    System.out.println("返回数据结构: code=" + result.getCode() + ", message=" + result.getMessage() + ", data=" + result.getData());
                    return result;
                } else {
                    System.err.println("AI调用错误: " + response.getMsg());

                    // 如果是频率限制错误，进行重试
                    if (response.getMsg().contains("Too many API requests") || 
                        response.getMsg().contains("频率") || 
                        response.getMsg().contains("限流") ||
                        response.getMsg().contains("overloaded") ||
                        response.getMsg().contains("429")) {
                        retryCount++;
                        // 指数退避算法，增加随机抖动
                        long backoffTime = BASE_RETRY_INTERVAL * (long) Math.pow(2, retryCount) + random.nextInt(1000);
                        System.out.println("AI调用频率过高，正在重试... (" + retryCount + "/" + MAX_RETRIES + ")");
                        System.out.println("退避时间: " + backoffTime + "ms");
                        Thread.sleep(backoffTime);
                        // 重试时重新获取令牌
                        if (!tryAcquireToken()) {
                            continue;
                        }
                        continue;
                    }

                    // 其他错误，直接返回
                    R errorResult = new R();
                    errorResult.setCode(500);
                    errorResult.setMessage("ai调用失败: " + response.getMsg());
                    System.out.println("返回错误结果: " + errorResult.getCode() + ", " + errorResult.getMessage());
                    return errorResult;
                }
            } catch (Exception e) {
                System.err.println("AI调用异常: " + e.getMessage());
                e.printStackTrace();

                // 如果是频率限制相关的异常，进行重试
                if (e.getMessage().contains("Too many API requests") || 
                    e.getMessage().contains("频率") || 
                    e.getMessage().contains("限流") ||
                    e.getMessage().contains("overloaded") ||
                    e.getMessage().contains("429") ||
                    e.getMessage().contains("HTTP 429")) {
                    retryCount++;
                    // 指数退避算法，增加随机抖动
                    long backoffTime = BASE_RETRY_INTERVAL * (long) Math.pow(2, retryCount) + random.nextInt(1000);
                    System.out.println("AI调用频率过高，正在重试... (" + retryCount + "/" + MAX_RETRIES + ")");
                    System.out.println("退避时间: " + backoffTime + "ms");
                    try {
                        Thread.sleep(backoffTime);
                        // 重试时重新获取令牌
                        if (!tryAcquireToken()) {
                            continue;
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue;
                }

                // 其他异常，直接返回
                R errorResult = new R();
                errorResult.setCode(500);
                errorResult.setMessage("ai服务异常: " + e.getMessage());
                System.out.println("返回异常结果: " + errorResult.getCode() + ", " + errorResult.getMessage());
                return errorResult;
            } finally {
                // 减少当前请求计数
                currentRequests.decrementAndGet();
            }
        }

        // 重试次数用完，返回错误
        R errorResult = new R();
        errorResult.setCode(503);
        errorResult.setMessage("当前AI请求过于繁忙，请稍后再试");
        System.out.println("返回错误结果: " + errorResult.getCode() + ", " + errorResult.getMessage());
        return errorResult;
    }

    @Autowired
    public DefaulteProperties defaulteProperties;

    public String getdesc(String imgname) throws IOException {
        if (imgname == null || imgname.trim().isEmpty()) {
            return "用户未上传图片";
        }

        String apiKey = ""; // 请填写您自己的APIKey
        ZhipuAiClient client = ZhipuAiClient.builder().ofZHIPU()
                .apiKey(apiKey)
                .build();

        String filepath=defaulteProperties.getUploadfilepath()+imgname;
        byte[] bytes = Files.readAllBytes(new File(filepath).toPath());
        String base64 = Base64.getEncoder().encodeToString(bytes);

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model("glm-4.6v-flash")
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content(Arrays.asList(
                                        MessageContent.builder()
                                                .type("text")
                                                .text("简要描述下这张图片,只输出简要描述后的就行了")
                                                .build(),
                                        MessageContent.builder()
                                                .type("image_url")
                                                .imageUrl(ImageUrl.builder()
                                                        .url(base64)
                                                        .build())
                                                .build()))
                                .build()))
                .build();

        ChatCompletionResponse response = client.chat().createChatCompletion(request);
        Object reply=null;
        if (response.isSuccess()) {
             reply = response.getData().getChoices().get(0).getMessage();
        } else {
            System.err.println("错误: " + response.getMsg());
        }
      return reply.toString();
    }

    // 辅助方法：从工具结果中提取截图URL
    private String extractScreenshotUrl(String toolResults) {
        try {
            // 简单的字符串匹配，查找screenshot字段
            int startIndex = toolResults.indexOf("\"screenshot\":\"");
            if (startIndex == -1) {
                return null;
            }
            startIndex += "\"screenshot\":\"".length();

            // 查找结束引号
            int endIndex = toolResults.indexOf("\"", startIndex);
            if (endIndex == -1) {
                return null;
            }

            return toolResults.substring(startIndex, endIndex);
        } catch (Exception e) {
            System.err.println("提取截图URL失败: " + e.getMessage());
            return null;
        }
    }

    // 辅助方法：解析工具参数
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseArguments(String argsStr) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(argsStr);
            return mapper.convertValue(node, Map.class);
        } catch (Exception e) {
            System.err.println("解析参数失败: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // 辅助方法：格式化 MCP 结果（压缩版，减少token消耗）
    private String formatMcpResult(Map<String, Object> mcpResult) {
        if (mcpResult == null) {
            return "工具执行完成，但未返回结果";
        }

        try {
            // 压缩处理：只保留关键信息
            Map<String, Object> compressedResult = new HashMap<>();
            
            // 处理content字段
            if (mcpResult.containsKey("content")) {
                Object content = mcpResult.get("content");
                if (content instanceof List) {
                    List<?> contentList = (List<?>) content;
                    if (!contentList.isEmpty()) {
                        Object firstContent = contentList.get(0);
                        if (firstContent instanceof Map) {
                            // 对于Map类型的内容，只保留关键字段
                            Map<?, ?> contentMap = (Map<?, ?>) firstContent;
                            Map<String, Object> simplifiedContent = new HashMap<>();
                            
                            // 保留错误信息
                            if (contentMap.containsKey("error")) {
                                simplifiedContent.put("error", contentMap.get("error"));
                            }
                            
                            // 保留文本内容，限制长度
                            if (contentMap.containsKey("text")) {
                                Object text = contentMap.get("text");
                                if (text instanceof String) {
                                    String textStr = (String) text;
                                    // 限制文本长度，避免token消耗过大
                                    if (textStr.length() > 1000) {
                                        simplifiedContent.put("text", textStr.substring(0, 1000) + "...（内容已截断）");
                                    } else {
                                        simplifiedContent.put("text", textStr);
                                    }
                                }
                            }
                            
                            if (!simplifiedContent.isEmpty()) {
                                compressedResult.put("content", List.of(simplifiedContent));
                            }
                        } else if (firstContent instanceof String) {
                            // 对于String类型的内容，限制长度
                            String contentStr = (String) firstContent;
                            if (contentStr.length() > 1000) {
                                compressedResult.put("content", List.of(contentStr.substring(0, 1000) + "...（内容已截断）"));
                            } else {
                                compressedResult.put("content", List.of(contentStr));
                            }
                        } else {
                            // 其他类型，简单转换为字符串
                            compressedResult.put("content", List.of(firstContent.toString()));
                        }
                    }
                }
            }
            
            // 保留错误状态
            if (mcpResult.containsKey("isError")) {
                compressedResult.put("isError", mcpResult.get("isError"));
            }
            
            if (compressedResult.isEmpty()) {
                return "工具执行成功";
            }
            
            // 转换为简洁的JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(compressedResult);
        } catch (Exception e) {
            // 出错时返回简洁的错误信息
            return "工具执行结果处理失败: " + e.getMessage();
        }
    }

    

    /**
     * 流式AI对话接口 - 使用SSE实时输出
     * @return SseEmitter
     */
    @GetMapping(value = "/takeaiStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter takeaiStream(
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String imgone,
            @RequestParam(required = false, defaultValue = "default") String conversationId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        // 在新线程中处理请求
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String userMessage = message;
                String desctext = imgone != null ? getdesc(imgone) : "用户未上传图片";

                System.out.println("收到流式请求: " + userMessage + ", 对话ID: " + conversationId);
                
                // 发送开始消息
                emitter.send(SseEmitter.event()
                    .name("start")
                    .data("正在为您处理...")
                    .id(String.valueOf(System.currentTimeMillis())));
                
                // 获取或创建对话历史
                List<ChatMessage> messages = conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>());

                // 添加用户消息
                if (userMessage != null && !userMessage.isEmpty()) {
                    messages.add(ChatMessage.builder()
                        .role(ChatMessageRole.USER.value())
                        .content(userMessage)
                        .build());
                }
                
                // 流式循环调用工具
                int maxIterations = 10;
                int iteration = 0;
                List<ChatTool> aiTools = createAiTools(desctext);
                int maxRetries = 3;
                int retryDelay = 2000;
                
                while (iteration < maxIterations) {
                    iteration++;
                    
                    // 尝试获取令牌，如果没有令牌则等待后重试
                    int retryCount = 0;
                    while (!tryAcquireToken()) {
                        retryCount++;
                        if (retryCount >= maxRetries) {
                            emitter.send(SseEmitter.event()
                                .name("error")
                                .data("AI服务繁忙，请稍后再试"));
                            return;
                        }
                        try {
                            emitter.send(SseEmitter.event()
                                .name("thinking")
                                .data("AI服务繁忙，等待中... (" + retryCount + "/" + maxRetries + ")")
                                .id(String.valueOf(System.currentTimeMillis())));
                            Thread.sleep(retryDelay * retryCount);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    
                    // 创建请求
                    List<ChatMessage> requestMessages = new ArrayList<>();
                    requestMessages.add(ChatMessage.builder()
                        .role("system")
                        .content("浏览器助手。图片:"+desctext+"。规则:1.只在本系统操作 2.持续执行直到满足需求 3.用工具:截图/读页面/点击/填写")
                        .build());
                    requestMessages.addAll(messages);
                    
                    ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                        .model("glm-4.6v-flash")
                        .messages(requestMessages)
                        .tools(aiTools)
                        .build();
                    
                    // 调用AI（带重试机制）
                    ChatCompletionResponse response = null;
                    boolean apiSuccess = false;
                    for (int apiRetry = 0; apiRetry < maxRetries; apiRetry++) {
                        try {
                            // 发送尝试消息
                            emitter.send(SseEmitter.event()
                                .name("thinking")
                                .data("正在请求AI服务... (" + (apiRetry + 1) + "/" + maxRetries + ")")
                                .id(String.valueOf(System.currentTimeMillis())));
                            
                            response = client.chat().createChatCompletion(request);
                            if (response.isSuccess()) {
                                apiSuccess = true;
                                break;
                            }
                            // 如果是429错误，增加等待时间
                            if (response.getMsg() != null && (response.getMsg().contains("429") || response.getMsg().contains("overloaded") || response.getMsg().contains("too many requests"))) {
                                if (apiRetry < maxRetries - 1) {
                                    // 发送等待消息
                                    emitter.send(SseEmitter.event()
                                        .name("thinking")
                                        .data("AI服务繁忙，等待中... (" + (apiRetry + 1) + "/" + maxRetries + ")")
                                        .id(String.valueOf(System.currentTimeMillis())));
                                    // 指数退避策略
                                    int waitTime = retryDelay * (int) Math.pow(2, apiRetry);
                                    System.out.println("[AI调用] 429错误，等待" + waitTime + "毫秒后重试");
                                    Thread.sleep(waitTime);
                                } else {
                                    // 最后一次重试失败
                                    System.out.println("[AI调用] 429错误，重试次数用尽");
                                    emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data("AI服务繁忙，请稍后再试"));
                                }
                            } else {
                                break;
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        } catch (retrofit2.adapter.rxjava3.HttpException e) {
                            // 捕获Retrofit HTTP异常（放在ZAiHttpException之前）
                            System.out.println("[AI调用] 捕获到Retrofit HttpException: " + e.getMessage());
                            System.out.println("[AI调用] HTTP状态码: " + e.code());
                            if (e.code() == 429) {
                                if (apiRetry < maxRetries - 1) {
                                    // 发送等待消息
                                    try {
                                        emitter.send(SseEmitter.event()
                                            .name("thinking")
                                            .data("AI服务繁忙，等待中... (" + (apiRetry + 1) + "/" + maxRetries + ")")
                                            .id(String.valueOf(System.currentTimeMillis())));
                                        // 指数退避策略
                                        int waitTime = retryDelay * (int) Math.pow(2, apiRetry);
                                        System.out.println("[AI调用] 429错误，等待" + waitTime + "毫秒后重试");
                                        Thread.sleep(waitTime);
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                } else {
                                    // 最后一次重试失败
                                    System.out.println("[AI调用] 429错误，重试次数用尽");
                                    emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data("AI服务繁忙，请稍后再试"));
                                }
                            } else {
                                // 其他HTTP异常
                                System.out.println("[AI调用] 其他HTTP异常，状态码: " + e.code());
                                if (apiRetry == maxRetries - 1) {
                                    emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data("AI调用异常: HTTP " + e.code() + " - " + e.getMessage()));
                                }
                                break;
                            }
                        } catch (ai.z.openapi.service.model.ZAiHttpException e) {
                            // 捕获HTTP异常
                            System.out.println("[AI调用] 捕获到ZAiHttpException: " + e.getMessage());
                            if (e.getMessage() != null && (e.getMessage().contains("429") || e.getMessage().contains("overloaded") || e.getMessage().contains("too many requests"))) {
                                if (apiRetry < maxRetries - 1) {
                                    // 发送等待消息
                                    try {
                                        emitter.send(SseEmitter.event()
                                            .name("thinking")
                                            .data("AI服务繁忙，等待中... (" + (apiRetry + 1) + "/" + maxRetries + ")")
                                            .id(String.valueOf(System.currentTimeMillis())));
                                        // 指数退避策略
                                        int waitTime = retryDelay * (int) Math.pow(2, apiRetry);
                                        System.out.println("[AI调用] 429错误，等待" + waitTime + "毫秒后重试");
                                        Thread.sleep(waitTime);
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                } else {
                                    // 最后一次重试失败
                                    System.out.println("[AI调用] 429错误，重试次数用尽");
                                    emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data("AI服务繁忙，请稍后再试"));
                                }
                            } else {
                                // 其他HTTP异常
                                System.out.println("[AI调用] 其他HTTP异常: " + e.getMessage());
                                if (apiRetry == maxRetries - 1) {
                                    emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data("AI调用异常: " + e.getMessage()));
                                }
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println("[AI调用] 捕获到其他异常: " + e.getMessage());
                            e.printStackTrace();
                            if (apiRetry == maxRetries - 1) {
                                emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("AI调用异常: " + e.getMessage()));
                            }
                        }
                    }
                    
                    if (!apiSuccess || response == null || !response.isSuccess()) {
                        // 检查是否已经通过异常处理发送了错误消息
                        if (response != null) {
                            String errorMsg = response.getMsg();
                            if (errorMsg != null && !(errorMsg.contains("429") || errorMsg.contains("overloaded"))) {
                                emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("AI调用失败: " + errorMsg));
                            }
                        } else if (apiSuccess) {
                            emitter.send(SseEmitter.event()
                                .name("error")
                                .data("AI调用失败: 未知错误"));
                        }
                        break;
                    }
                    
                    ChatMessage aiMessage = response.getData().getChoices().get(0).getMessage();
                    Object reply = aiMessage.getContent();
                    List<ToolCalls> toolCallsList = aiMessage.getToolCalls();

                    // 添加AI回复到历史
                    messages.add(aiMessage);
                    
                    // 如果有工具调用
                    if (toolCallsList != null && !toolCallsList.isEmpty()) {
                        for (ToolCalls toolCalls : toolCallsList) {
                            ChatFunctionCall functionCall = toolCalls.getFunction();
                            if (functionCall != null) {
                                String toolName = functionCall.getName();
                                
                                // 检查toolName是否为空
                                if (toolName == null || toolName.isEmpty()) {
                                    emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data("工具名称为空，无法执行")
                                        .id(String.valueOf(System.currentTimeMillis())));
                                    messages.add(ChatMessage.builder()
                                        .role("tool")
                                        .content("工具执行失败: 工具名称为空")
                                        .build());
                                    continue;
                                }

                                // 发送正在执行的消息
                                emitter.send(SseEmitter.event()
                                    .name("thinking")
                                    .data("正在执行: " + toolName)
                                    .id(String.valueOf(System.currentTimeMillis())));

                                try {
                                    Object argsData = functionCall.getArguments();
                                    Map<String, Object> arguments;
                                    if (argsData instanceof String) {
                                        arguments = parseArguments((String) argsData);
                                    } else if (argsData instanceof Map) {
                                        arguments = (Map<String, Object>) argsData;
                                    } else {
                                        arguments = new HashMap<>();
                                    }

                                    Map<String, Object> mcpResult = mcpService.callTool(toolName, arguments);
                                    String resultContent = formatMcpResult(mcpResult);

                                    messages.add(ChatMessage.builder()
                                        .role("tool")
                                        .content(resultContent)
                                        .build());

                                } catch (Exception e) {
                                    emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data("工具执行失败: " + e.getMessage()));
                                    messages.add(ChatMessage.builder()
                                        .role("tool")
                                        .content("工具执行失败: " + e.getMessage())
                                        .build());
                                }
                            }
                        }
                        // 执行完工具后，继续循环，让AI根据工具结果决定下一步操作
                        continue;
                    } else {
                        // 没有工具调用，给出最终回复
                        if (reply instanceof String) {
                            // 流式发送文本
                            String textReply = (String) reply;
                            for (int i = 0; i < textReply.length(); i++) {
                                emitter.send(SseEmitter.event()
                                    .name("content")
                                    .data(String.valueOf(textReply.charAt(i)))
                                    .id(String.valueOf(System.currentTimeMillis())));
                                Thread.sleep(30); // 模拟打字效果
                            }
                        }
                        break; // 结束循环
                    }
                }
                
                // 发送完成消息
                emitter.send(SseEmitter.event()
                    .name("end")
                    .data("处理完成")
                    .id(String.valueOf(System.currentTimeMillis())));
                
                // 释放令牌
                releaseToken();
                
                emitter.complete();
                
            } catch (Exception e) {
                System.err.println("流式处理异常: " + e.getMessage());
                e.printStackTrace();
                try {
                    emitter.send(SseEmitter.event()
                        .name("error")
                        .data("处理异常: " + e.getMessage()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                // 释放令牌（出错时也要释放）
                releaseToken();
                emitter.completeWithError(e);
            } finally {
                executor.shutdown();
            }
        });
        
        return emitter;
    }
    
    /**
     * 创建AI工具列表
     */
    private List<ChatTool> createAiTools(String desctext) {
        List<ChatTool> aiTools = new ArrayList<>();

        aiTools.add(ChatTool.builder()
            .type("function")
            .function(ChatFunction.builder()
                .name("chrome_screenshot")
                .description("Take a screenshot")
                .parameters(ChatFunctionParameters.builder()
                    .type("object")
                    .properties(Map.of())
                    .required(Arrays.asList())
                    .build())
                .build())
            .build());

        aiTools.add(ChatTool.builder()
            .type("function")
            .function(ChatFunction.builder()
                .name("chrome_read_page")
                .description("Read page elements")
                .parameters(ChatFunctionParameters.builder()
                    .type("object")
                    .properties(Map.of())
                    .required(Arrays.asList())
                    .build())
                .build())
            .build());
        
        aiTools.add(ChatTool.builder()
            .type("function")
            .function(ChatFunction.builder()
                .name("chrome_click_element")
                .description("Click an element")
                .parameters(ChatFunctionParameters.builder()
                    .type("object")
                    .properties(Map.of(
                        "selector", ChatFunctionParameterProperty.builder().type("string").description("CSS selector").build()
                    ))
                    .required(Arrays.asList("selector"))
                    .build())
                .build())
            .build());
        
        aiTools.add(ChatTool.builder()
            .type("function")
            .function(ChatFunction.builder()
                .name("chrome_fill_or_select")
                .description("Fill or select form element")
                .parameters(ChatFunctionParameters.builder()
                    .type("object")
                    .properties(Map.of(
                        "selector", ChatFunctionParameterProperty.builder().type("string").description("CSS selector").build(),
                        "value", ChatFunctionParameterProperty.builder().type("string").description("Value to fill").build()
                    ))
                    .required(Arrays.asList("selector", "value"))
                    .build())
                .build())
            .build());
        
        return aiTools;
    }

    /**
     * 查询商店所拥有的菜品分类
     * @param params
     * @return
     */
    @GetMapping("/selectcategoryforshop")
    public R selectCategoryForShop(@RequestParam(required = false) Map<String, Object> params) {
        // 检查并获取pageNo参数，默认为1
        Object pageNoObj = params.get("pageNo");
        int pageno = 1; // 默认页码为1
        if (pageNoObj != null) {
            pageno = Integer.parseInt(pageNoObj.toString());
        }

        // 检查并获取categoryname参数，默认为空字符串
        Object categorynameObj = params.get("categoryname");
        String categoryname = ""; // 默认搜索条件为空
        if (categorynameObj != null) {
            categoryname = categorynameObj.toString();
        }

        // 第一步：先查询符合条件的唯一商店ID列表，使用PageHelper分页
        PageHelper.startPage(pageno, 5);
        List<Long> shopIds = service.selectShopIdsByCategory(categoryname);
        PageInfo<Long> idPageInfo = new PageInfo<>(shopIds);

        // 第二步：根据商店ID列表查询完整的商店信息和分类
        List<Shop> shopList;
        if (shopIds.isEmpty()) {
            shopList = new ArrayList<>();
        } else {
            shopList = service.selectShopWithCategoriesByIds(shopIds);
        }

        // 第三步：创建包含正确分页信息的PageInfo对象
        PageInfo<Shop> pageInfo = new PageInfo<>(shopList);
        pageInfo.setTotal(idPageInfo.getTotal());
        pageInfo.setPageNum(idPageInfo.getPageNum());
        pageInfo.setPageSize(idPageInfo.getPageSize());
        pageInfo.setPages(idPageInfo.getPages());

        return new R().addData("pageinfo", pageInfo);
    }
    /**
     * 查询所有商店和销量
     * @return
     */
    @PostMapping("/selectallandProduct")
    public R selectallandcount(@RequestBody Map<String,Object> selectwhere) throws IOException {
        List where = (List) selectwhere.get("where");
        QueryWrapper<Shop> queryWrapper = commontUtil.getWhere(where);

        int pageSize = 10;
        if (selectwhere.get("pageSize") != null) {
            pageSize = Integer.parseInt(selectwhere.get("pageSize").toString());
        }

        int page = 1;
        if (selectwhere.get("page") != null) {
            page = Integer.parseInt(selectwhere.get("page").toString());
        }

        // 第一步：先查询符合条件的商家总数
        long total = shopService.count(queryWrapper);

        // 第二步：使用PageHelper分页查询商家ID列表
        PageHelper.startPage(page, pageSize);
        queryWrapper.select("id");
        List<Long> shopIds = shopService.listObjs(queryWrapper, obj -> Long.valueOf(obj.toString()));

        // 第三步：如果没有商家数据，直接返回空结果
        if (shopIds.isEmpty()) {
            return new R().addData("pageinfo", new PageInfo<>(shopIds));
        }

        // 第四步：查询商家和商品信息
        List<Shop> shopList = shopService.selectShopsWithProductsByIds(shopIds);

        // 第五步：创建包含正确分页信息的PageInfo对象
        PageInfo<Shop> pageInfo = new PageInfo<>(shopList);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPages((int) Math.ceil((double) total / pageSize));

        return new R().addData("pageinfo", pageInfo);
    }
}
