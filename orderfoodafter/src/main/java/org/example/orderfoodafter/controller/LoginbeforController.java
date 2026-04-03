// 定义登录前控制器的包路径
package org.example.orderfoodafter.controller;
// 导入智谱AI服务模型类
import ai.z.openapi.service.model.*;
// 导入langchain4j工具相关类
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
// 导入MyBatis Plus的查询条件包装器类
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入PageHelper分页插件
import com.github.pagehelper.Page;
// 导入PageHelper分页插件
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类
import com.github.pagehelper.PageInfo;
// 导入默认属性配置类
import org.example.orderfoodafter.common.DefaulteProperties;
// 导入通用响应结果类
import org.example.orderfoodafter.common.R;
// 导入WhereEntity查询实体类
import org.example.orderfoodafter.common.WhereEntity;
// 导入商品实体类
import org.example.orderfoodafter.entity.Product;
// 导入店铺实体类
import org.example.orderfoodafter.entity.Shop;
// 导入商品服务接口
import org.example.orderfoodafter.service.ProductService;
// 导入店铺服务接口
import org.example.orderfoodafter.service.ShopService;
// 导入Spring的自动装配注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的媒体类型类
import org.springframework.http.MediaType;
// 导入Spring的SSE事件发送器类
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
// 导入Java的File类
import java.io.File;
// 导入Java的IO异常类
import java.io.IOException;
// 导入Java的文件工具类
import java.nio.file.Files;
// 导入Java的LocalDate类
import java.time.LocalDate;
// 导入Java的LocalDateTime类
import java.time.LocalDateTime;
// 导入Java的日期时间格式化类
import java.time.format.DateTimeFormatter;
// 导入Java的Util包下的所有类
import java.util.*;
// 导入Java的并发包下的所有类
import java.util.concurrent.*;
// 导入Spring的请求映射注解集合
import org.springframework.web.bind.annotation.*;
// 导入Java的原子长整型类
import java.util.concurrent.atomic.AtomicLong;
// 导入Java的原子整型类
import java.util.concurrent.atomic.AtomicInteger;
// 导入Jackson TypeReference用于JSON解析
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 登录前控制器
 * 负责处理用户登录前的相关操作，包括店铺查询、商品推荐、AI智能问答等功能
 * 
 * @author 李梦瑶
 * @date 2026-03-18
 */
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义该控制器的请求路径前缀为/Shopforbefor
@RequestMapping("/Shopforbefor")
// 定义登录前控制器类，继承基础控制器，指定实体类型为Shop，服务类型为ShopService

public class LoginbeforController extends BaseController<Shop, ShopService> {
    // 自动注入店铺服务，使用依赖注入方式获取实例
    @Autowired
    private ShopService shopService;
    // 自动注入商品服务，使用依赖注入方式获取实例
    @Autowired
    private ProductService productService;
    // 自动注入评价映射器，使用依赖注入方式获取实例
    @Autowired
    private org.example.orderfoodafter.mapper.EvaluationMapper evaluationMapper;
    // 自动注入MCP服务，使用依赖注入方式获取实例
    @Autowired
    private org.example.orderfoodafter.service.McpService mcpService;
    // 自动注入AI服务，使用依赖注入方式获取实例
    @Autowired
    private org.example.orderfoodafter.service.AiService aiService;
    // 注入ChatLanguageModel用于工具调用
    @Autowired
    private ChatLanguageModel chatLanguageModel;
    // Jackson ObjectMapper用于JSON解析
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    /**
     * 构造函数
     *
     * @param shopService 店铺服务
     * @author 李梦瑶
     */
    public LoginbeforController(ShopService shopService) {
        // 调用父类构造函数，传入服务实例
        super(shopService);
        // 将传入的服务实例赋值给本地变量
        this.shopService = shopService;
    }

    /**
     * 查询销量前十的商店
     *
     * @return 商店列表
     * @author 李梦瑶
     */
    // 定义处理查询销量前十商店的GET请求接口，路径为/selectshopbytop30
    @GetMapping("/selectshopbytop30")
    public R selectshopbytop30() {
        // 调用服务查询销量前十的商店列表
        List<Shop> shopList = shopService.selectShopBytop();
        // 返回商店列表数据
        /**
         * R
         *
         * @author 李梦瑶
         */
        return new R().addData("shopList", shopList);
    }


    /**
     * 多条件查询商店
     *
     * @param id       商店ID
     * @param cid      分类ID
     * @param nickname 昵称
     * @param phone    电话
     * @param page     页码
     * @return 查询结果
     * @author 李梦瑶
     */
    // 定义处理多条件查询订单的POST请求接口，路径为/selectshopbylist
    @PostMapping("/selectshopbylist")
    public R selectshopbyid(@RequestParam("id") int id, @RequestParam("cid") int cid, @RequestParam("nickname") String nickname, @RequestParam("phone") String phone, @RequestParam("page") String page) {
        // 初始化订单页码
        int orderpage = 0;
        // 判断页码是否为空
        if (page == null) {
            // 如果为空，设置为1
            orderpage = 1;
        } else {
            // 否则转换为整数
            orderpage = Integer.parseInt(page);
        }
        // 启动分页，每页显示6条记录
        Page<Object> obj = PageHelper.startPage(orderpage, 6);
        // 调用服务根据条件查询商店列表
        List<Shop> shopList = shopService.selectShopBylist(id, cid, nickname, phone);
        // 将商店列表封装为分页信息对象
        PageInfo pageInfo = new PageInfo<>(shopList);
        // 返回商店列表和分页信息数据
        /**
         * R
         *
         * @author 李梦瑶
         */
        return new R().addData("shopList", shopList).addData("obj", pageInfo);
    }

    /**
     * 查询整个平台前五的菜品
     *
     * @return 菜品列表
     * @author 李梦瑶
     */
    // 定义处理查询前五菜品的GET请求接口，路径为/selectproducttop5
    @GetMapping("/selectproducttop5")
    public R selectproducttop5() {
        // 调用服务查询前五的商品列表
        List<Product> productList = productService.selectproducttop5();
        // 返回商品列表数据
        /**
         * R
         *
         * @author 李梦瑶
         */
        return new R().addData("productList", productList);
    }

    // 重试次数
    private static final int MAX_RETRIES = 5;
    // 初始重试间隔（毫秒）
    private static final long BASE_RETRY_INTERVAL = 2000;

    // 令牌桶算法参数
    private static final long REFILL_RATE = 1; // 每秒生成1个令牌
    private static final long BUCKET_CAPACITY = 5; // 桶容量
    /**
     * AtomicLong方法
     *
     * @author 李梦瑶
     */
    private static final AtomicLong availableTokens = new AtomicLong(BUCKET_CAPACITY); // 当前可用令牌数
    /**
     * AtomicLong
     *
     * @author 李梦瑶
     */
    private static final AtomicLong lastRefillTime = new AtomicLong(System.currentTimeMillis()); // 上次令牌补充时间

    // 简单的并发限流，使用AtomicInteger记录当前请求数
    /**
     * AtomicInteger
     *
     * @author 李梦瑶
     */
    private static final AtomicInteger currentRequests = new AtomicInteger(0);
    private static final int MAX_CONCURRENT_REQUESTS = 3;

    // 请求缓存，使用ConcurrentHashMap存储用户请求和对应的AI响应
    // 键：用户输入文本，值：AI响应结果
    // 设置缓存过期时间为30分钟
    private static final ConcurrentHashMap<String, CacheEntry> requestCache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_TIME = 30 * 60 * 1000; // 30分钟

    /**
     * 缓存条目类
     *
     * @author 李梦瑶
     */
    private static class CacheEntry {
        private final String response;
        private final long timestamp;

        /**
         * 构造函数
         *
         * @param response 响应内容
         * @author 李梦瑶
         */
        public CacheEntry(String response) {
            this.response = response;
            this.timestamp = System.currentTimeMillis();
        }

        /**
         * 判断缓存是否过期
         *
         * @return 是否过期
         * @author 李梦瑶
         */
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME;
        }

        /**
         * 获取响应内容
         *
         * @return 响应内容
         * @author 李梦瑶
         */
        public String getResponse() {
            return response;
        }
    }

    /**
     * 令牌桶算法：补充令牌
     *
     * @author 李梦瑶
     */
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

    /**
     * 令牌桶算法：尝试获取令牌
     *
     * @return 是否获取成功
     * @author 李梦瑶
     */
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

    // 存储对话历史，用于多轮工具调用（使用langchain4j的ChatMessage）
    private static final ConcurrentHashMap<String, List<dev.langchain4j.data.message.ChatMessage>> conversationHistory = new ConcurrentHashMap<>();

    /**
     * AI对话接口
     *
     * @param text1 请求参数
     * @return AI响应
     * @throws IOException 异常信息
     * @author 李梦瑶
     */
    @PostMapping("/takeai")
    public R takeai(@RequestBody Map<String, String> text1) throws IOException {
        // 移除JSON字符串的引号（如果前端发送的是带引号的字符串）
        String text = text1.get("message");
        String conversationId = text1.get("conversationId"); // 对话ID，用于区分不同会话
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = "default";
        }

        System.out.println("收到用户输入: " + text + ", 对话ID: " + conversationId);

        String imgname = text1.get("imgone");
        String desctext = null;
        if (imgname != null) {
            try {
                desctext = aiService.getImageDescription(imgname);
            } catch (Exception e) {
                System.err.println("获取图片描述失败: " + e.getMessage());
                desctext = "图片描述获取失败";
            }
        } else {
            desctext = "用户未上传图片";
        }
        System.out.println("图片描述: " + desctext);

        // 获取工具结果
        String toolResults = text1.get("toolResults");

        // 判断是否需要使用智谱AI（有图片或工具调用结果包含截图）
        boolean hasImage = (imgname != null && !imgname.isEmpty());
        boolean hasScreenshot = toolResults != null && toolResults.contains("screenshot");
        boolean needsZhipuAi = hasImage || hasScreenshot;

        // 检查message参数是否为空（但如果有toolResults，则允许message为空）
        if ((text == null || text.trim().isEmpty()) && (toolResults == null || toolResults.isEmpty())) {
            /**
             * R
             *
             * @author 李梦瑶
             */
            R errorResult = new R();
            errorResult.setCode(400);
            errorResult.setMessage("请输入有效的对话内容");
            return errorResult;
        }

        // 获取或创建对话历史
        List<dev.langchain4j.data.message.ChatMessage> messages = conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>());

        // 添加用户消息
        if (text != null && !text.isEmpty()) {
            messages.add(dev.langchain4j.data.message.UserMessage.from(text));
        }

        // 【重要】当有工具执行结果时，跳过缓存检查，继续对话
        if (toolResults == null || toolResults.isEmpty()) {
            // 先检查缓存，减少重复AI调用
            CacheEntry cachedEntry = requestCache.get(text);
            if (cachedEntry != null) {
                if (!cachedEntry.isExpired()) {
                    System.out.println("从缓存中获取AI响应");
                    /**
                     * R
                     *
                     * @author 李梦瑶
                     */
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
            /**
             * R
             *
             * @author 李梦瑶
             */
            R errorResult = new R();
            errorResult.setCode(503);
            errorResult.setMessage("当前AI请求过多，请稍后再试");
            return errorResult;
        }

        // 简单的并发限流，防止系统过载
        if (currentRequests.get() >= MAX_CONCURRENT_REQUESTS) {
            /**
             * R
             *
             * @author 李梦瑶
             */
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

        // 【核心逻辑】根据用户意图选择是否使用工具
        // 1. 如果用户明确要求操作浏览器（点击、填写表单、导航等），使用MCP工具
        // 2. 如果只是普通对话，直接使用langchain4j

        // 检查用户是否要求操作浏览器
        boolean requiresBrowserAction = requiresBrowserAction(text);

        if (requiresBrowserAction) {
            // 用户要求操作浏览器，使用MCP工具
            System.out.println("检测到浏览器操作请求，使用MCP工具");
            /**
             * handleBrowserAction
             *
             * @author 李梦瑶
             */
            return handleBrowserAction(text, desctext, messages, conversationId);
        }

        // 普通对话，直接使用langchain4j
        /**
         * println
         *
         * @author 李梦瑶
         */
        System.out.println("普通对话，使用 langchain4j (qwen3-coder-plus)");

        // 构建包含图片描述的提示词
        String fullPrompt = text;
        if (!"用户未上传图片".equals(desctext) && !"图片描述获取失败".equals(desctext)) {
            fullPrompt = "用户上传的图片描述: " + desctext + "\n\n用户问题: " + text;
        }

        try {
            // 使用 langchain4j 调用AI
            String response = aiService.chatWithLangChain4j(
                    List.of(dev.langchain4j.data.message.UserMessage.from(fullPrompt))
            );

            // 缓存结果
            /**
             * put
             *
             * @author 李梦瑶
             */
            requestCache.put(text, new CacheEntry(response));

            // 添加到对话历史
            messages.add(dev.langchain4j.data.message.AiMessage.from(response));
            conversationHistory.put(conversationId, messages);

            /**
             * R
             *
             * @author 李梦瑶
             */
            return new R().addData("aitake", response).addData("conversationId", conversationId);

        } catch (Exception e) {
            /**
             * R
             *
             * @author 李梦瑶
             */
            R errorResult = new R();
            errorResult.setCode(500);
            errorResult.setMessage("AI调用失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 检查用户是否要求操作浏览器
     *
     * @param text 用户输入文本
     * @return 是否需要浏览器操作
     * @author 李梦瑶
     */
    private boolean requiresBrowserAction(String text) {
        // 扩展关键词列表，包含更多可能触发浏览器操作的关键词
        String[] keywords = {
                "点击", "填写", "导航", "打开", "访问", "截图", "查看页面", "登录", "注册", "提交", "选择",
                "查看", "余额", "账户", "资金", "操作", "控制", "自动化", "浏览器", "网页", "网站",
                "搜索", "查询", "获取", "进入", "跳转", "刷新"
        };
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理浏览器操作
     *
     * @param text           用户输入文本
     * @param desctext       图片描述
     * @param messages       对话历史
     * @param conversationId 对话ID
     * @return 操作结果
     * @author 李梦瑶
     */
    private R handleBrowserAction(String text, String desctext, List<dev.langchain4j.data.message.ChatMessage> messages, String conversationId) {
        try {
            // 创建包含MCP工具规范的请求
            String fullPrompt = text;
            if (!"用户未上传图片".equals(desctext) && !"图片描述获取失败".equals(desctext)) {
                fullPrompt = "用户上传的图片描述: " + desctext + "\n\n用户请求: " + text;
            }

            fullPrompt += "\n\n【重要提示】你现在拥有浏览器操作能力，必须主动使用工具来完成任务！\n" +
                    "可用工具：\n" +
                    "1. chrome_screenshot: 截取当前页面截图\n" +
                    "2. chrome_read_page: 读取页面可见元素\n" +
                    "3. chrome_click_element: 点击页面元素（需要selector参数）\n" +
                    "4. chrome_fill_or_select: 填写表单或选择选项（需要selector和value参数）\n" +
                    "5. chrome_navigate: 导航到指定URL（需要url参数）\n\n" +
                    "【强制执行流程】\n" +
                    "1. 首先使用chrome_read_page读取当前页面内容\n" +
                    "2. 检查页面中是否有'营养大师'弹窗或类似的弹窗元素\n" +
                    "3. 如果有'营养大师'弹窗，先使用chrome_click_element关闭该弹窗\n" +
                    "4. 截图查看当前页面状态\n" +
                    "5. 根据用户需求执行相应操作（点击、填写、导航等）\n" +
                    "6. 如果需要，多次截图和读取页面来确认操作结果\n" +
                    "7. 将操作结果清晰反馈给用户\n\n" +
                    "8.如果用户要对个人信息进行操作点击我的" +
                    "【注意事项】\n" +
                    "- 不要只用文字解释，必须实际使用工具操作浏览器\n" +
                    "- 每次执行任何操作前，必须先读取页面内容\n" +
                    "- 如果发现'营养大师'弹窗，必须先关闭它\n" +
                    "- 如果工具执行失败，尝试其他方法或告诉用户无法完成\n" +
                    "- 如果用户不需要操作浏览器，直接回复，不要调用mcp工具" +
                    "- 对于查看余额、登录、导航等任务，必须使用浏览器工具完成";

            // 创建langchain4j的ChatMessage列表
            List<dev.langchain4j.data.message.ChatMessage> langchainMessages = new ArrayList<>();

            // 添加系统消息
            langchainMessages.add(dev.langchain4j.data.message.SystemMessage.from(
                    "你是智能浏览器操作助手，拥有浏览器自动化能力。用户要求你操作浏览器完成任务时，" +
                            "你必须主动使用提供的工具来实际操作浏览器，而不是只提供文字说明。"
            ));

            // 添加用户消息
            langchainMessages.add(dev.langchain4j.data.message.UserMessage.from(fullPrompt));

            // 添加历史对话消息
            langchainMessages.addAll(messages);

            // 执行完整的工具调用循环
            String response = aiService.executeToolLoop(
                    langchainMessages,
                    createMcpToolSpecifications(),
                    mcpService,
                    5 // 最大重试次数
            );

            // 更新对话历史
            messages.clear();
            messages.addAll(langchainMessages);
            conversationHistory.put(conversationId, messages);

            /**
             * R
             *
             * @author 李梦瑶
             */
            return new R().addData("aitake", response).addData("conversationId", conversationId);

        } catch (Exception e) {
            /**
             * R
             *
             * @author 李梦瑶
             */
            R errorResult = new R();
            errorResult.setCode(500);
            errorResult.setMessage("浏览器操作失败: " + e.getMessage());
            return errorResult;
        }
    }

    @Autowired
    public DefaulteProperties defaulteProperties;

    /**
     * 辅助方法：从工具结果中提取截图URL
     *
     * @param toolResults 工具结果
     * @return 截图URL
     * @author 李梦瑶
     */
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

    /**
     * 辅助方法：解析工具参数
     *
     * @param argsStr 参数字符串
     * @return 解析后的参数映射
     * @author 李梦瑶
     */
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

    /**
     * 流式AI对话接口 - 使用SSE实时输出
     *
     * @param message        用户消息
     * @param imgone         图片
     * @param conversationId 对话ID
     * @return SseEmitter
     * @author 李梦瑶
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

                // 获取图片描述（使用智谱AI）
                String desctext = "用户未上传图片";
                if (imgone != null && !imgone.isEmpty()) {
                    try {
                        desctext = aiService.getImageDescription(imgone);
                    } catch (Exception e) {
                        System.err.println("获取图片描述失败: " + e.getMessage());
                        desctext = "图片描述获取失败";
                    }
                }

                System.out.println("收到流式请求: " + userMessage + ", 对话ID: " + conversationId);
                System.out.println("图片描述: " + desctext);

                // 发送开始消息
                emitter.send(SseEmitter.event()
                        .name("start")
                        .data("正在为您处理...")
                        .id(String.valueOf(System.currentTimeMillis())));

                // 获取或创建对话历史
                List<dev.langchain4j.data.message.ChatMessage> messages = conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>());

                // 添加用户消息
                if (userMessage != null && !userMessage.isEmpty()) {
                    messages.add(dev.langchain4j.data.message.UserMessage.from(userMessage));
                }

                // 【核心逻辑】根据用户意图选择是否使用工具
                // 1. 如果用户明确要求操作浏览器（点击、填写表单、导航等），使用MCP工具
                // 2. 如果只是普通对话，直接使用langchain4j流式

                // 检查用户是否要求操作浏览器
                boolean requiresBrowserAction = requiresBrowserAction(userMessage);

                if (requiresBrowserAction) {
                    // 用户要求操作浏览器，使用MCP工具
                    System.out.println("流式对话 - 检测到浏览器操作请求，使用MCP工具");
                    handleBrowserActionStream(emitter, userMessage, desctext, messages, conversationId);
                    return;
                }

                // 普通对话，使用langchain4j流式
                // 构建包含图片描述的提示词
                String fullPrompt = userMessage;
                if (!"用户未上传图片".equals(desctext) && !"图片描述获取失败".equals(desctext)) {
                    fullPrompt = "用户上传的图片描述: " + desctext + "\n\n用户问题: " + userMessage;
                }

                /**
                 * println
                 *
                 * @author 李梦瑶
                 */
                System.out.println("流式对话 - 使用 langchain4j (qwen3-coder-plus)");

                try {
                    // 使用 langchain4j 流式调用AI
                    aiService.chatWithLangChain4jStream(
                            List.of(dev.langchain4j.data.message.UserMessage.from(fullPrompt)),
                            new dev.langchain4j.model.chat.response.StreamingChatResponseHandler() {
                                /**
                                 * StringBuilder方法
                                 *
                                 * @author 李梦瑶
                                 */
                                private final StringBuilder fullResponse = new StringBuilder();

                                @Override
                                /**
                                 * onPartialResponse
                                 *
                                 * @author 李梦瑶
                                 */
                                public void onPartialResponse(String partialResponse) {
                                    try {
                                        // 实时发送每个token到前端
                                        emitter.send(SseEmitter.event()
                                                .name("content")
                                                .data(partialResponse)
                                                .id(String.valueOf(System.currentTimeMillis())));
                                        fullResponse.append(partialResponse);
                                    } catch (IOException e) {
                                        System.err.println("发送SSE消息失败: " + e.getMessage());
                                    }
                                }

                                /**
                                 * onCompleteResponse方法
                                 *
                                 * @author 李梦瑶
                                 */

                                @Override
                                public void onCompleteResponse(dev.langchain4j.model.chat.response.ChatResponse completeResponse) {
                                    try {
                                        // 添加到对话历史
                                        messages.add(dev.langchain4j.data.message.AiMessage.from(fullResponse.toString()));
                                        conversationHistory.put(conversationId, messages);

                                        // 发送完成消息
                                        emitter.send(SseEmitter.event()
                                                .name("end")
                                                .data("处理完成")
                                                .id(String.valueOf(System.currentTimeMillis())));
                                        emitter.complete();
                                    } catch (IOException e) {
                                        System.err.println("发送完成消息失败: " + e.getMessage());
                                        emitter.completeWithError(e);
                                    }
                                }

                                /**
                                 * onError方法
                                 *
                                 * @author 李梦瑶
                                 */

                                @Override
                                public void onError(Throwable error) {
                                    try {
                                        emitter.send(SseEmitter.event()
                                                .name("error")
                                                .data("处理异常: " + error.getMessage()));
                                        emitter.completeWithError(error);
                                    } catch (IOException e) {
                                        System.err.println("发送错误消息失败: " + e.getMessage());
                                    }
                                }
                            }
                    );
                } catch (Exception e) {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data("处理异常: " + e.getMessage()));
                        emitter.completeWithError(e);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } finally {
                    executor.shutdown();
                }
            } catch (Exception e) {
                System.err.println("流式处理异常: " + e.getMessage());
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("处理异常: " + e.getMessage()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                emitter.completeWithError(e);
                executor.shutdown();
            }
        });

        return emitter;
    }

    /**
     * 查询商店所拥有的菜品分类
     *
     * @param params 查询参数
     * @return 查询结果
     * @author 李梦瑶
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

        /**
         * R
         *
         * @author 李梦瑶
         */
        return new R().addData("pageinfo", pageInfo);
    }

    /**
     * 查询所有商店和销量
     *
     * @param selectwhere 查询条件
     * @return 查询结果
     * @throws IOException 异常信息
     * @author 李梦瑶
     */
    @PostMapping("/selectallandShop")
    public R selectallandcount(@RequestBody Map<String, Object> selectwhere) throws IOException {
        // Map 集合转换 List
        List where = (List) selectwhere.get("where");
        //封装queryWrapper
        QueryWrapper<Shop> queryWrapper = commontUtil.getWhere(where);
        //声明整数类型变量 存储每页查询多少条数据 int
        int pageSize = 10;
        //转换成 int 类型 if pageSize Integer
        if (selectwhere.get("pageSize") != null) {

            pageSize = Integer.parseInt(selectwhere.get("pageSize").toString());
        }

        int page = 1;
        if (selectwhere.get("page") != null) {
            page = Integer.parseInt(selectwhere.get("page").toString());
        }

        // 第一步：先查询符合条件的商家总数 lonng
        long total = shopService.count(queryWrapper);

        // 第二步：使用PageHelper分页查询商家ID列表 PageHelper q List
        PageHelper.startPage(page, pageSize);
        queryWrapper.select("id");
        List<Long> shopIds = shopService.listObjs(queryWrapper, obj -> Long.valueOf(obj.toString()));

        // 第三步：如果没有商家数据，直接返回空结果 if return
        if (shopIds.isEmpty()) {
            return new R().addData("pageinfo", new PageInfo<>(shopIds));
        }

        // 第四步：查询商家和商品信息/List
        List<Shop> shopList = shopService.selectShopsWithProductsByIds(shopIds);

        // 第五步：创建包含正确分页信息的PageInfo对象
        PageInfo<Shop> pageInfo = new PageInfo<>(shopList);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPages((int) Math.ceil((double) total / pageSize));

        return new R().addData("pageinfo", pageInfo);
    }

    /**
     * 创建MCP工具规范
     * @return MCP工具规范列表
     * @author 李梦瑶
     */
    private List<ToolSpecification> createMcpToolSpecifications() {
        List<ToolSpecification> tools = new ArrayList<>();

        // chrome_screenshot 工具
        tools.add(ToolSpecification.builder()
                .name("chrome_screenshot")
                .description("截取当前页面的截图")
                .parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder()
                        .build())
                .build());

        // chrome_read_page 工具
        tools.add(ToolSpecification.builder()
                .name("chrome_read_page")
                .description("读取页面的可见元素")
                .parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder()
                        .build())
                .build());

        // chrome_click_element 工具
        tools.add(ToolSpecification.builder()
                .name("chrome_click_element")
                .description("点击页面元素")
                .parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder()
                        .addStringProperty("selector", "CSS选择器")
                        .required("selector")
                        .build())
                .build());

        // chrome_fill_or_select 工具
        tools.add(ToolSpecification.builder()
                .name("chrome_fill_or_select")
                .description("填写表单或选择选项")
                .parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder()
                        .addStringProperty("selector", "CSS选择器")
                        .addStringProperty("value", "要填写的值")
                        .required("selector", "value")
                        .build())
                .build());

        // chrome_navigate 工具
        tools.add(ToolSpecification.builder()
                .name("chrome_navigate")
                .description("导航到指定URL。【警告】仅用于在当前订单系统内导航（如从首页到商品详情页），绝对不要导航到百度、谷歌等外部网站！只能导航到本系统的页面！")
                .parameters(dev.langchain4j.model.chat.request.json.JsonObjectSchema.builder()
                        .addStringProperty("url", "要导航的URL（仅限当前系统内的URL，如/shop/list、/product/detail/123等）")
                        .required("url")
                        .build())
                .build());

        return tools;
    }
    
    /**
     * 处理流式浏览器操作 - 实时发送工具执行过程
     */
    private void handleBrowserActionStream(SseEmitter emitter, String userMessage, String desctext, 
                                           List<dev.langchain4j.data.message.ChatMessage> messages, String conversationId) {
        try {
            String fullPrompt = userMessage;
            if (!"用户未上传图片".equals(desctext) && !"图片描述获取失败".equals(desctext)) {
                fullPrompt = "用户上传的图片描述: " + desctext + "\n\n用户请求: " + userMessage;
            }
            
            fullPrompt += "\n\n【重要提示】你现在拥有浏览器操作能力，必须主动使用工具来完成任务！\n" +
                    "【强制要求】\n" +
                    "- **必须调用MCP工具**来完成用户的请求！" +
                    "- **不要编造工具执行过程**，要真正调用工具！" +
                    "- 如果不调用工具，任务无法完成！" +
                    "- 工具执行过程会自动显示给用户，你不需要输出工具执行过程！" +
                    "- 你只需要在工具执行完成后，总结结果给用户！\n\n" +
                    "【关键限制 - 必须遵守】\n" +
                    "⚠️ 用户是在当前订单系统（外卖点餐系统）内操作，**绝对不要跳转到外部网站**！\n" +
                    "⚠️ **禁止**使用chrome_navigate导航到百度、谷歌等外部搜索引擎或其他外部网站！\n" +
                    "⚠️ 只能在当前系统的页面内操作（店铺列表、商品详情、购物车、结算页面等）！\n" +
                    "⚠️ 如果用户说\"点奶茶\"，是指在当前系统中选择奶茶店铺，然后点餐，不是去外面搜索！\n" +
                    "⚠️ 如果用户说\"查看余额\"，是指在当前系统的个人中心查看，不是去银行网站！\n" +
                    "⚠️ 如果用户说\"登录\"，是指在当前系统的登录页面登录，不是去其他网站！\n" +
                    "⚠️ **如果当前页面无法完成任务，直接告诉用户当前页面无法完成，不要跳转外部网站！**\n\n" +
                    "可用工具：\n" +
                    "1. chrome_screenshot: 截取当前页面截图\n" +
                    "2. chrome_read_page: 读取页面可见元素\n" +
                    "3. chrome_click_element: 点击页面元素（需要selector参数）\n" +
                    "4. chrome_fill_or_select: 填写表单或选择选项（需要selector和value参数）\n" +
                    "5. chrome_navigate: **仅用于在当前系统内导航**，如从首页到商品详情页（需要url参数）\n\n" +
                    "【强制执行流程】\n" +
                    "1. 首先使用chrome_read_page读取当前页面内容\n" +
                    "2. 检查页面中是否有'营养大师'弹窗或类似的弹窗元素\n" +
                    "3. 如果有'营养大师'弹窗，先使用chrome_click_element关闭该弹窗\n" +
                    "4. 截图查看当前页面状态\n" +
                    "5. 根据用户需求在当前系统内执行相应操作（点击、填写、导航等）\n" +
                    "6. 如果需要，多次截图和读取页面来确认操作结果\n" +
                    "7. 将操作结果清晰反馈给用户\n\n" +
                    "8.如果用户要对个人信息进行操作点击我的"+
                    "【禁止事项】\n" +
                    "- **绝对禁止编造工具执行过程**！不要编造截图路径、工具名称等！\n" +
                    "- **不要手动编写工具执行过程**，工具执行过程由系统自动显示\n" +
                    "- **只回复最终结果**，不要包含任何工具执行过程的描述\n" +
                    "- 如果看到工具执行结果，直接基于结果回复用户\n" +
                    "- 如果没有调用工具，直接告诉用户无法完成\n\n" +
                    "- 如果用户不需要操作浏览器，直接回复,不要用mcp工具"+
                    "【注意事项】\n" +
                    "- 不要只用文字解释，必须实际使用工具操作浏览器\n" +
                    "- 每次执行任何操作前，必须先读取页面内容\n" +
                    "- 如果发现'营养大师'弹窗，必须先关闭它\n" +
                    "- 如果工具执行失败，尝试其他方法或告诉用户无法完成\n" +
                    "- **所有操作必须在当前系统内完成，严禁跳转外部网站**";
            
            // 创建langchain4j的ChatMessage列表
            List<dev.langchain4j.data.message.ChatMessage> langchainMessages = new ArrayList<>();
            
            // 添加系统消息
            langchainMessages.add(dev.langchain4j.data.message.SystemMessage.from(
                "你是智能浏览器操作助手，拥有浏览器自动化能力。用户要求你操作浏览器完成任务时，" +
                "你必须主动使用提供的工具来实际操作浏览器，而不是只提供文字说明。" +
                "【关键】用户在当前订单系统（外卖点餐系统）内操作，绝对不要跳转到外部网站！" +
                "所有操作必须在当前系统内完成，禁止导航到百度、谷歌等外部搜索引擎或其他外部网站。" +
                "【项目信息】" +
                "- 你只能操作这个项目（外卖点餐系统），不能操作其他任何平台！" +
                "- 如果用户说'点奶茶'，是指在当前系统内选择奶茶店铺，然后点餐，不是去外面搜索！" +
                "- 如果用户说'查看余额'，是指在当前系统的个人中心查看，不是去银行网站！" +
                "- 如果用户说'登录'，是指在当前系统的登录页面登录，不是去其他网站！" +
                "- 如果用户问'网站是什么'，回答'外卖点餐系统（orderfood项目）'！" +
                "- 永远不要告诉用户'无法获取'，你要操作的就是这个项目！" +
                "【强制要求】" +
                "- **必须调用MCP工具**！不要编造工具执行过程！" +
                "- **不要手动编写工具执行过程**，工具执行过程由系统自动显示！" +
                "- **只回复最终总结**，不要包含任何工具执行过程的描述！" +
                "- 如果不调用工具，无法完成任务！" +
                "- 你必须真正使用工具，不能编造结果！"+ "如果用户要对个人信息进行操作点击我的"
                   +"- 如果用户不需要操作浏览器，直接回复,不要用mcp工具"
            ));
            
            // 添加用户消息
            langchainMessages.add(dev.langchain4j.data.message.UserMessage.from(fullPrompt));
            
            // 添加历史对话消息
            langchainMessages.addAll(messages);
            
            // 获取工具规范
            System.out.println("[handleBrowserActionStream] 准备创建工具规范...");
            List<ToolSpecification> tools = createMcpToolSpecifications();
            System.out.println("[handleBrowserActionStream] 工具规范创建完成，工具数量: " + tools.size());
            
            // 自己实现工具调用循环，实时发送进度
            int iteration = 0;
            int consecutiveFailures = 0;
            
            System.out.println("[handleBrowserActionStream] 开始工具调用循环...");
            
            while (true) {
                iteration++;
                System.out.println("[handleBrowserActionStream] 工具调用循环 " + iteration + " 开始");
                
                // 实时发送进度
                System.out.println("[handleBrowserActionStream] 发送进度事件...");
                emitter.send(SseEmitter.event()
                    .name("progress")
                    .data("\n=== 工具调用循环 " + iteration + " ===")
                    .id(String.valueOf(System.currentTimeMillis())));
                System.out.println("[handleBrowserActionStream] 进度事件发送成功");
                
                // 调用AI
                System.out.println("[handleBrowserActionStream] 准备调用AI...");
                System.out.println("[handleBrowserActionStream] 消息数量: " + langchainMessages.size());
                System.out.println("[handleBrowserActionStream] 工具数量: " + tools.size());
                
                ChatRequest request = ChatRequest.builder()
                        .messages(langchainMessages)
                        .toolSpecifications(tools)
                        .build();
                
                System.out.println("[handleBrowserActionStream] ChatRequest构建完成，开始调用AI...");
                
                var response = chatLanguageModel.chat(request);
                
                System.out.println("[handleBrowserActionStream] AI调用完成");
                
                AiMessage aiMessage = response.aiMessage();
                System.out.println("[handleBrowserActionStream] AI消息: " + aiMessage.text());
                System.out.println("[handleBrowserActionStream] 是否有工具调用请求: " + aiMessage.hasToolExecutionRequests());
                
                // 检查是否有工具调用请求
                if (!aiMessage.hasToolExecutionRequests()) {
                    // 没有工具调用，AI给出最终回复
                    System.out.println("[handleBrowserActionStream] AI没有请求工具，发送最终回复...");
                    emitter.send(SseEmitter.event()
                        .name("progress")
                        .data("\nAI不再请求工具，给出最终回复...")
                        .id(String.valueOf(System.currentTimeMillis())));
                    
                    langchainMessages.add(aiMessage);
                    
                    // 流式发送最终回复
                    for (int i = 0; i < aiMessage.text().length(); i++) {
                        emitter.send(SseEmitter.event()
                            .name("content")
                            .data(String.valueOf(aiMessage.text().charAt(i)))
                            .id(String.valueOf(System.currentTimeMillis())));
                        Thread.sleep(10);
                    }
                    
                    emitter.send(SseEmitter.event()
                        .name("end")
                        .data("处理完成")
                        .id(String.valueOf(System.currentTimeMillis())));
                    emitter.complete();
                    return;
                }
                
                // 有工具调用，实时发送工具信息
                emitter.send(SseEmitter.event()
                    .name("progress")
                    .data("\nAI请求调用工具，工具数量: " + aiMessage.toolExecutionRequests().size())
                    .id(String.valueOf(System.currentTimeMillis())));
                
                langchainMessages.add(aiMessage);
                
                // 执行所有工具调用，实时发送进度
                boolean allToolsSucceeded = true;
                for (dev.langchain4j.agent.tool.ToolExecutionRequest toolRequest : aiMessage.toolExecutionRequests()) {
                    String toolName = toolRequest.name();
                    String toolArgs = toolRequest.arguments();
                    
                    // 实时发送工具执行开始
                    emitter.send(SseEmitter.event()
                        .name("tool_start")
                        .data("\n🔧 正在执行工具: " + toolName + "\n📝 参数: " + toolArgs)
                        .id(String.valueOf(System.currentTimeMillis())));
                    
                    int retryCount = 0;
                    boolean toolSucceeded = false;
                    
                    // 工具调用失败重试机制
                    while (retryCount < 5 && !toolSucceeded) {
                        retryCount++;
                        
                        // 实时发送重试信息
                        if (retryCount > 1) {
                            emitter.send(SseEmitter.event()
                                .name("progress")
                                .data("\n🔄 重试执行工具，次数: " + retryCount + "/5")
                                .id(String.valueOf(System.currentTimeMillis())));
                        }
                        
                        try {
                            // 解析参数
                            Map<String, Object> arguments = parseToolArguments(toolArgs);
                            
                            // 对于截图工具，强制设置参数避免下载文件
                            if ("chrome_screenshot".equals(toolName)) {
                                arguments.put("savePng", false);
                                arguments.put("storeBase64", true);
                                
                                // 实时发送参数调整信息
                                emitter.send(SseEmitter.event()
                                    .name("progress")
                                    .data("\n⚙️  已自动调整参数: savePng=false, storeBase64=true（避免下载文件）")
                                    .id(String.valueOf(System.currentTimeMillis())));
                            }
                            
                            // 实时发送调用MCP服务信息
                            emitter.send(SseEmitter.event()
                                .name("progress")
                                .data("\n⏳ 正在调用MCP服务执行浏览器操作...")
                                .id(String.valueOf(System.currentTimeMillis())));
                            
                            // 调用MCP工具
                            Map<String, Object> mcpResult = mcpService.callTool(toolName, arguments);
                            
                            // 将工具执行结果添加到对话历史
                            String resultContent = formatMcpResult(mcpResult);
                            
                            // 检查是否是截图工具，如果是则进行图片识别
                            if ("chrome_screenshot".equals(toolName) && mcpResult.containsKey("screenshotPath")) {
                                try {
                                    String screenshotPath = mcpResult.get("screenshotPath").toString();
                                    
                                    // 实时发送AI识别进度
                                    emitter.send(SseEmitter.event()
                                        .name("progress")
                                        .data("\n📸 截图已保存，正在启动AI识别...")
                                        .id(String.valueOf(System.currentTimeMillis())));
                                    
                                    // 调用图片识别
                                    String imageDescription = aiService.getImageDescription(screenshotPath);
                                    
                                    // 将图片识别结果添加到工具执行结果中
                                    resultContent += "\n\n🤖 【AI图片识别】\n" + imageDescription;
                                } catch (Exception e) {
                                    resultContent += "\n\n❌ 【AI图片识别失败】" + e.getMessage();
                                }
                            }
                            
                            langchainMessages.add(dev.langchain4j.data.message.ToolExecutionResultMessage.from(
                                toolRequest, resultContent
                            ));
                            
                            // 实时发送工具执行结果
                            emitter.send(SseEmitter.event()
                                .name("tool_result")
                                .data(resultContent)
                                .id(String.valueOf(System.currentTimeMillis())));
                            
                            // 实时发送成功信息
                            emitter.send(SseEmitter.event()
                                .name("progress")
                                .data("\n✅ 工具执行成功")
                                .id(String.valueOf(System.currentTimeMillis())));
                            
                            toolSucceeded = true;
                            consecutiveFailures = 0;
                            
                        } catch (Exception e) {
                            emitter.send(SseEmitter.event()
                                .name("tool_error")
                                .data("\n❌ 工具执行失败: " + e.getMessage())
                                .id(String.valueOf(System.currentTimeMillis())));
                            
                            if (retryCount < 5) {
                                // 实时发送准备重试信息
                                emitter.send(SseEmitter.event()
                                    .name("progress")
                                    .data("\n⏳ 等待2秒后重试...")
                                    .id(String.valueOf(System.currentTimeMillis())));
                                Thread.sleep(2000);
                                continue;
                            } else {
                                // 重试次数耗尽
                                allToolsSucceeded = false;
                                consecutiveFailures++;
                                
                                // 实时发送失败信息
                                emitter.send(SseEmitter.event()
                                    .name("progress")
                                    .data("\n⚠️  工具执行失败，已达到最大重试次数（5次）")
                                    .id(String.valueOf(System.currentTimeMillis())));
                                
                                // 如果连续失败5次，停止迭代
                                if (consecutiveFailures >= 5) {
                                    emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data("工具连续失败" + consecutiveFailures + "次，停止迭代")
                                        .id(String.valueOf(System.currentTimeMillis())));
                                    emitter.complete();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("[handleBrowserActionStream] 异常发生: " + e.getMessage());
            System.err.println("[handleBrowserActionStream] 异常类型: " + e.getClass().getName());
            e.printStackTrace();
            try {
                emitter.send(SseEmitter.event()
                    .name("error")
                    .data("浏览器操作失败: " + e.getMessage())
                    .id(String.valueOf(System.currentTimeMillis())));
                emitter.completeWithError(e);
            } catch (IOException ioException) {
                System.err.println("[handleBrowserActionStream] 发送错误消息失败: " + ioException.getMessage());
                ioException.printStackTrace();
            }
        }
    }
    
    /**
     * 解析工具参数字符串
     */
    private Map<String, Object> parseToolArguments(String argumentsJson) {
        try {
            return objectMapper.readValue(argumentsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    /**
     * 格式化MCP结果为可读字符串
     */
    private String formatMcpResult(Map<String, Object> result) {
            /**
     * StringBuilder
     * 
     * @author 李梦瑶
     */
        StringBuilder sb = new StringBuilder();
        sb.append("【工具执行结果】\n");
        
        if (result.containsKey("success") && Boolean.TRUE.equals(result.get("success"))) {
            sb.append("✅ 执行成功\n");
        } else if (result.containsKey("error")) {
            sb.append("❌ 执行失败\n");
            sb.append("错误信息: ").append(result.get("error")).append("\n");
        }
        
        if (result.containsKey("screenshotPath")) {
            sb.append("📸 截图已保存: ").append(result.get("screenshotPath")).append("\n");
        }
        
        if (result.containsKey("data")) {
            sb.append("返回数据: ").append(result.get("data")).append("\n");
        }
        
        if (result.containsKey("message")) {
            sb.append("执行消息: ").append(result.get("message")).append("\n");
        }
        
        return sb.toString();
    }
}
