package org.example.orderfoodafter.service;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.example.orderfoodafter.common.DefaulteProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MCPService接口
 * 提供MCP相关的业务逻辑处理功能
 * 注意：MCP Chrome Bridge服务器现在支持多实例连接
 *
 * @author 李梦瑶
 * @date 2026-02-28
 */
@Service
public class McpService {

    @Value("${mcp.server.url}")
    private String mcpServerUrl;

    @Autowired
    private DefaulteProperties defaulteProperties;
    
    // ObjectMapper用于JSON序列化
/**
 * ObjectMapper方法
 *
 * @author 李吉隆
 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 查找 Node.js 可执行文件路径
     */
    private String findNodeExecutable() {
        System.out.println("[MCP] 开始查找 Node.js 可执行文件...");
        
        String[] possiblePaths = {
            "node",
            "node.exe",
            "/usr/local/bin/node",
            "/usr/bin/node",
            System.getProperty("user.home") + "/AppData/Roaming/npm/node.cmd",
            System.getProperty("user.home") + "/AppData/Roaming/npm/node.exe",
            "C:\\Program Files\\nodejs\\node.exe",
                /**
     * Files
     * 
     * @author 李吉隆
     */
            "C:\\Program Files (x86)\\nodejs\\node.exe"
        };
        
        for (String path : possiblePaths) {
            try {
                System.out.println("[MCP] 尝试路径: " + path);
                
                java.io.File file = new java.io.File(path);
                if (file.exists()) {
                    String absolutePath = file.getAbsolutePath();
                    System.out.println("[MCP] ✓ 找到 Node.js: " + absolutePath);
                    return absolutePath;
                }
                
                ProcessBuilder pb;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        /**
     * ProcessBuilder
     * 
     * @author 李吉隆
     */
                    pb = new ProcessBuilder("cmd", "/c", "where", path);
                } else {
                        /**
     * ProcessBuilder
     * 
     * @author 李吉隆
     */
                    pb = new ProcessBuilder("which", path);
                }
                
                Process process = pb.start();
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream())
                );
                String result = reader.readLine();
                if (result != null && new java.io.File(result).exists()) {
                    System.out.println("[MCP] ✓ 通过命令找到 Node.js: " + result);
                    return result;
                }
                process.waitFor();
            } catch (Exception e) {
                System.out.println("[MCP] ✗ 路径 " + path + " 查找失败: " + e.getMessage());
            }
        }
        
        System.out.println("[MCP] ✗ 未能找到 Node.js 可执行文件");
        return null;
    }
    
    /**
     * 查找 MCP Chrome Server 路径（stdio版本）
     */
    private String findMcpChromeServer() {
        System.out.println("[MCP] 开始查找 MCP Chrome Bridge (stdio版本)...");
        String userHome = System.getProperty("user.home");

        // 优先查找stdio版本
        String[] possiblePaths = {
            // Windows路径
            userHome + "\\AppData\\Roaming\\npm\\node_modules\\mcp-chrome-bridge\\dist\\mcp\\mcp-server-stdio.js",
            userHome + "\\node_modules\\mcp-chrome-bridge\\dist\\mcp\\mcp-server-stdio.js",
            ".\\node_modules\\mcp-chrome-bridge\\dist\\mcp\\mcp-server-stdio.js",
            "..\\node_modules\\mcp-chrome-bridge\\dist\\mcp\\mcp-server-stdio.js",
            // Linux/Mac路径
            userHome + "/.config/nvm/versions/node/v24.13.0/lib/node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js",
            userHome + "/AppData/Roaming/npm/node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js",
            userHome + "/node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js",
            "./node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js",
            "../node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js"
        };

        for (String path : possiblePaths) {
            System.out.println("[MCP] 尝试路径: " + path);
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                String absolutePath = file.getAbsolutePath();
                System.out.println("[MCP] ✓ 找到 MCP Chrome Bridge (stdio): " + absolutePath);
                return absolutePath;
            } else {
                System.out.println("[MCP] ✗ 文件不存在: " + path);
            }
        }

        System.out.println("[MCP] ✗ 未能找到 MCP Chrome Bridge (stdio版本)");
        System.out.println("[MCP] 提示: 请运行 'npm install -g mcp-chrome-bridge' 安装MCP Chrome Bridge");
        return null;
    }

    /**
     * 停止可能存在的MCP Chrome Bridge HTTP服务器
     * 避免端口冲突
     */
    private void stopExistingHttpMcpServer() {
        System.out.println("[MCP] 检查是否有运行中的MCP Chrome Bridge HTTP服务器...");

        try {
            ProcessBuilder pb;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                pb = new ProcessBuilder("netstat", "-ano");
            } else {
                pb = new ProcessBuilder("netstat", "-tulpn");
            }

            Process process = pb.start();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );

            String line;
            java.util.Set<Integer> pidsToKill = new java.util.HashSet<>();

            while ((line = reader.readLine()) != null) {
                // 查找监听端口12306的进程
                if (line.contains("12306") && line.contains("LISTENING")) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 5) {
                        try {
                            int pid = Integer.parseInt(parts[parts.length - 1]);
                            pidsToKill.add(pid);
                            System.out.println("[MCP] 发现监听端口12306的进程: PID " + pid);
                        } catch (NumberFormatException e) {
                            // 忽略解析错误
                        }
                    }
                }
            }

            // 停止这些进程
            for (Integer pid : pidsToKill) {
                try {
                    System.out.println("[MCP] 停止进程 PID " + pid);
                    ProcessBuilder killPb;
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        killPb = new ProcessBuilder("taskkill", "/F", "/PID", String.valueOf(pid));
                    } else {
                        killPb = new ProcessBuilder("kill", "-9", String.valueOf(pid));
                    }
                    killPb.start().waitFor();
                    System.out.println("[MCP] ✓ 已停止进程 PID " + pid);
                } catch (Exception e) {
                    System.err.println("[MCP] ✗ 停止进程失败: " + e.getMessage());
                }
            }

            if (pidsToKill.isEmpty()) {
                System.out.println("[MCP] 未发现运行中的MCP Chrome Bridge HTTP服务器");
            }

        } catch (Exception e) {
            System.err.println("[MCP] 检查现有进程时出错: " + e.getMessage());
        }
    }

    /**
     * 创建新的MCP客户端（每次调用都创建新实例）
     * 使用stdio方式连接MCP Chrome Bridge
     */
    private McpSyncClient createMcpClient() {
        int maxRetries = 3;
        int retryCount = 0;
        McpSyncClient client = null;

        while (retryCount < maxRetries) {
            try {
                System.out.println("========================================");
                System.out.println("创建MCP客户端 (stdio方式)");
                System.out.println("========================================");
                System.out.println("尝试 " + (retryCount + 1) + "/" + maxRetries);
                System.out.println("时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()));

                String nodePath = findNodeExecutable();
                String mcpChromeStdioPath = findMcpChromeServer();

                System.out.println("\n[步骤1] 查找 Node.js 和 MCP Server...");
                if (nodePath == null) {
                    throw new RuntimeException("无法找到 Node.js，请确保已正确安装");
                }
                if (mcpChromeStdioPath == null) {
                    throw new RuntimeException("无法找到 MCP Chrome Server，请运行 'npm install -g mcp-chrome-bridge' 安装");
                }

                System.out.println("  Node.js 路径: " + nodePath);
                System.out.println("  MCP Server 路径: " + mcpChromeStdioPath);

                java.io.File nodeFile = new java.io.File(nodePath);
                java.io.File mcpFile = new java.io.File(mcpChromeStdioPath);
                if (!nodeFile.exists()) {
                    throw new RuntimeException("Node.js 文件不存在: " + nodePath);
                }
                if (!mcpFile.exists()) {
                    throw new RuntimeException("MCP服务器文件不存在: " + mcpChromeStdioPath);
                }

                System.out.println("  Node.js 文件存在: 是");
                System.out.println("  MCP Server 文件存在: 是");

                Map<String, String> environment = new HashMap<>();
                environment.put("NODE_ENV", "production");
                environment.put("MCP_SESSION_ID", String.valueOf(System.currentTimeMillis()));

                System.out.println("\n[步骤2] 构建服务器参数...");
                ServerParameters serverParams = ServerParameters.builder(nodePath)
                    .args(List.of(mcpChromeStdioPath))
                    .env(environment)
                    .build();

                JacksonMcpJsonMapper jsonMapper = new JacksonMcpJsonMapper(objectMapper);
                StdioClientTransport transport = new StdioClientTransport(serverParams, jsonMapper);

                System.out.println("[步骤3] 创建 MCP 客户端实例...");
                client = McpClient.sync(transport)
                    .requestTimeout(Duration.ofSeconds(120))
                    .build();

                System.out.println("[步骤4] 初始化 MCP 客户端...");
                long startTime = System.currentTimeMillis();
                client.initialize();
                long initTime = System.currentTimeMillis() - startTime;

                System.out.println("[步骤5] MCP 客户端初始化成功");
                System.out.println("  初始化耗时: " + initTime + "ms");
                System.out.println("========================================\n");
                
                break;

            } catch (Exception e) {
                retryCount++;
                System.err.println("\n========================================");
                System.err.println("创建MCP客户端失败");
                System.err.println("========================================");
                System.err.println("尝试 " + retryCount + "/" + maxRetries);
                System.err.println("错误信息: " + e.getMessage());
                System.err.println("错误类型: " + e.getClass().getName());

                // 提供更有用的错误提示
                if (e.getMessage() != null && e.getMessage().contains("Process terminated with code 1")) {
                    System.err.println("\n[提示] MCP Chrome Bridge进程异常终止，可能原因：");
                    System.err.println("  1. MCP Chrome Bridge未正确安装");
                    System.err.println("  2. 端口冲突（如果其他实例正在运行）");
                    System.err.println("  3. Node.js版本不兼容");
                    System.err.println("  4. 配置文件错误");
                    System.err.println("\n[建议]");
                    System.err.println("  1. 检查是否已安装: npm list -g mcp-chrome-bridge");
                    System.err.println("  2. 重新安装: npm install -g mcp-chrome-bridge");
                    System.err.println("  3. 停止其他MCP Chrome Bridge进程");
                }

                System.err.println("========================================\n");
                e.printStackTrace();

                if (client != null) {
                    try {
                        client.close();
                    } catch (Exception closeEx) {
                        System.err.println("关闭失败的MCP客户端时出错: " + closeEx.getMessage());
                    }
                    client = null;
                }

                if (retryCount >= maxRetries) {
                    throw new RuntimeException("创建MCP客户端失败，已重试 " + maxRetries + " 次: " + e.getMessage(), e);
                }

                try {
                    System.out.println("等待 2 秒后重试...");
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("MCP客户端初始化被中断", ie);
                }
            }
        }

        return client;
    }

    /**
     * 列出所有可用的MCP工具（使用正确的MCP协议流程）
     */
    public List<Map<String, Object>> listTools() {
        System.out.println("========================================");
        System.out.println("获取 MCP 工具列表");
        System.out.println("========================================");
        System.out.println("调用时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()));
        
        long startTime = System.currentTimeMillis();
        String sessionId = null;
        
        try {
            java.net.URL mcpUrl = new java.net.URL("http://127.0.0.1:12306/mcp");
            
            // 步骤1: 发送initialize请求建立会话
            System.out.println("\n[步骤1] 发送initialize请求...");
            long initStart = System.currentTimeMillis();
            
            Map<String, Object> initRequest = new HashMap<>();
            initRequest.put("jsonrpc", "2.0");
            initRequest.put("id", 1);
            initRequest.put("method", "initialize");
            initRequest.put("params", Map.of(
                "protocolVersion", "2024-11-05",
                "capabilities", Map.of(),
                "clientInfo", Map.of(
                    "name", "SpringBootMCPClient",
                    "version", "1.0.0"
                )
            ));
            
            String initBody = objectMapper.writeValueAsString(initRequest);
            System.out.println("[步骤1] 请求体: " + initBody);
            
            java.net.HttpURLConnection initConn = (java.net.HttpURLConnection) mcpUrl.openConnection();
            initConn.setRequestMethod("POST");
            initConn.setRequestProperty("Content-Type", "application/json");
            initConn.setRequestProperty("Accept", "application/json, text/event-stream");
            initConn.setConnectTimeout(30000);
            initConn.setReadTimeout(30000);
            initConn.setDoOutput(true);
            
            java.io.OutputStreamWriter initWriter = new java.io.OutputStreamWriter(initConn.getOutputStream(), "UTF-8");
            initWriter.write(initBody);
            initWriter.flush();
            initWriter.close();
            
            int initResponseCode = initConn.getResponseCode();
            System.out.println("[步骤1] HTTP响应码: " + initResponseCode);
            
            if (initResponseCode != 200) {
                throw new RuntimeException("Initialize请求失败，响应码: " + initResponseCode);
            }
            
            java.io.BufferedReader initReader = new java.io.BufferedReader(
                new java.io.InputStreamReader(initConn.getInputStream(), "UTF-8")
            );
            
            // 读取SSE响应并提取data部分
            String initJsonData = null;
            String line;
            while ((line = initReader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    initJsonData = line.substring(5).trim();
                }
            }
            initReader.close();
            
            // 从响应头获取session ID
            List<String> sessionHeaders = initConn.getHeaderFields().get("mcp-session-id");
            if (sessionHeaders != null && !sessionHeaders.isEmpty()) {
                sessionId = sessionHeaders.get(0);
            }
            
            long initTime = System.currentTimeMillis() - initStart;
            System.out.println("[步骤1] Initialize成功，耗时: " + initTime + "ms");
            System.out.println("[步骤1] SSE响应内容: " + (initJsonData != null ? initJsonData : "未返回"));
            System.out.println("[步骤1] Session ID: " + (sessionId != null ? sessionId : "未返回"));
            
            // 步骤2: 发送initialized通知
            if (sessionId != null) {
                System.out.println("\n[步骤2] 发送initialized通知...");
                
                Map<String, Object> initializedRequest = Map.of(
                    "jsonrpc", "2.0",
                    "method", "notifications/initialized"
                );
                
                String initializedBody = objectMapper.writeValueAsString(initializedRequest);
                
                java.net.HttpURLConnection initializedConn = (java.net.HttpURLConnection) mcpUrl.openConnection();
                initializedConn.setRequestMethod("POST");
                initializedConn.setRequestProperty("Content-Type", "application/json");
                initializedConn.setRequestProperty("Accept", "application/json, text/event-stream");
                initializedConn.setRequestProperty("mcp-session-id", sessionId);
                initializedConn.setConnectTimeout(30000);
                initializedConn.setReadTimeout(30000);
                initializedConn.setDoOutput(true);
                
                java.io.OutputStreamWriter initializedWriter = new java.io.OutputStreamWriter(initializedConn.getOutputStream(), "UTF-8");
                initializedWriter.write(initializedBody);
                initializedWriter.flush();
                initializedWriter.close();
                
                initializedConn.getResponseCode();
                System.out.println("[步骤2] Initialized通知已发送");
            }
            
            // 步骤3: 发送tools/list请求
            System.out.println("\n[步骤3] 发送tools/list请求...");
            long listStart = System.currentTimeMillis();
            
            Map<String, Object> listRequest = Map.of(
                "jsonrpc", "2.0",
                "id", 2,
                "method", "tools/list",
                "params", Map.of()
            );
            
            String listBody = objectMapper.writeValueAsString(listRequest);
            System.out.println("[步骤3] 请求体: " + listBody);
            
            java.net.HttpURLConnection listConn = (java.net.HttpURLConnection) mcpUrl.openConnection();
            listConn.setRequestMethod("POST");
            listConn.setRequestProperty("Content-Type", "application/json");
            listConn.setRequestProperty("Accept", "application/json, text/event-stream");
            if (sessionId != null) {
                listConn.setRequestProperty("mcp-session-id", sessionId);
            }
            listConn.setConnectTimeout(30000);
            listConn.setReadTimeout(30000);
            listConn.setDoOutput(true);
            
            java.io.OutputStreamWriter listWriter = new java.io.OutputStreamWriter(listConn.getOutputStream(), "UTF-8");
            listWriter.write(listBody);
            listWriter.flush();
            listWriter.close();
            
            int listResponseCode = listConn.getResponseCode();
            System.out.println("[步骤3] HTTP响应码: " + listResponseCode);
            
            if (listResponseCode != 200) {
                throw new RuntimeException("Tools/list请求失败，响应码: " + listResponseCode);
            }
            
            java.io.BufferedReader listReader = new java.io.BufferedReader(
                new java.io.InputStreamReader(listConn.getInputStream(), "UTF-8")
            );
            
            // 读取SSE响应并提取data部分
            String listJsonData = null;
            while ((line = listReader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    listJsonData = line.substring(5).trim();
                }
            }
            listReader.close();
            
            if (listJsonData == null) {
                throw new RuntimeException("无法从SSE响应中提取data内容");
            }
            
            long listTime = System.currentTimeMillis() - listStart;
            System.out.println("[步骤3] 工具列表获取成功，耗时: " + listTime + "ms");
            System.out.println("[步骤3] SSE响应内容: " + listJsonData);
            
            // 解析工具列表
            List<Map<String, Object>> tools = new ArrayList<>();
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(listJsonData);
            
            if (jsonNode.has("result") && jsonNode.get("result").has("tools")) {
                com.fasterxml.jackson.databind.JsonNode toolsArray = jsonNode.get("result").get("tools");
                System.out.println("\n[步骤4] 解析工具信息...");
                System.out.println("找到 " + toolsArray.size() + " 个可用工具:");
                
                for (int i = 0; i < toolsArray.size(); i++) {
                    com.fasterxml.jackson.databind.JsonNode toolNode = toolsArray.get(i);
                    Map<String, Object> toolMap = new HashMap<>();
                    
                    String toolName = toolNode.has("name") ? toolNode.get("name").asText() : "";
                    String toolDesc = toolNode.has("description") ? toolNode.get("description").asText() : "";
                    
                    toolMap.put("name", toolName);
                    toolMap.put("description", toolDesc);
                    
                    if (toolNode.has("inputSchema")) {
                        toolMap.put("inputSchema", objectMapper.convertValue(toolNode.get("inputSchema"), Map.class));
                    }
                    
                    System.out.println("\n  工具 #" + (i + 1) + ":");
                    System.out.println("    名称: " + toolName);
                    System.out.println("    描述: " + toolDesc);
                    
                    tools.add(toolMap);
                }
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("\n========================================");
            System.out.println("MCP 工具列表获取成功");
            System.out.println("========================================");
            System.out.println("总耗时: " + totalTime + "ms");
            System.out.println("  - Initialize: " + initTime + "ms");
            System.out.println("  - List Tools: " + listTime + "ms");
            System.out.println("  - 工具总数: " + tools.size());
            System.out.println("========================================\n");
            
            return tools;
            
        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            System.err.println("\n========================================");
            System.err.println("获取 MCP 工具列表失败");
            System.err.println("========================================");
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("错误类型: " + e.getClass().getName());
            System.err.println("总耗时: " + errorTime + "ms");
            System.err.println("========================================\n");
            e.printStackTrace();
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("获取MCP工具列表失败", e);
        }
    }

    /**
     * 调用指定的MCP工具（使用正确的MCP协议流程）
     */
    public Map<String, Object> callTool(String toolName, Map<String, Object> arguments) {
        System.out.println("========================================");
        System.out.println("MCP 工具调用开始");
        System.out.println("========================================");
        System.out.println("工具名称: " + toolName);
        System.out.println("调用时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()));
        
        if (arguments != null && !arguments.isEmpty()) {
            System.out.println("工具参数:");
            try {
                    /**
     * println
     * 
     * @author 李吉隆
     */
                System.out.println("  " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(arguments));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                System.out.println("  [参数序列化失败] " + arguments);
            }
        } else {
            System.out.println("工具参数: 无");
        }
        
        long startTime = System.currentTimeMillis();
        String sessionId = null;
        
        try {
            java.net.URL mcpUrl = new java.net.URL("http://127.0.0.1:12306/mcp");
            
            // 步骤1: 发送initialize请求建立会话
            System.out.println("\n[步骤1] 发送initialize请求...");
            long initStart = System.currentTimeMillis();
            
            Map<String, Object> initRequest = new HashMap<>();
            initRequest.put("jsonrpc", "2.0");
            initRequest.put("id", 1);
            initRequest.put("method", "initialize");
            initRequest.put("params", Map.of(
                "protocolVersion", "2024-11-05",
                "capabilities", Map.of(),
                "clientInfo", Map.of(
                    "name", "SpringBootMCPClient",
                    "version", "1.0.0"
                )
            ));
            
            String initBody = objectMapper.writeValueAsString(initRequest);
            
            java.net.HttpURLConnection initConn = (java.net.HttpURLConnection) mcpUrl.openConnection();
            initConn.setRequestMethod("POST");
            initConn.setRequestProperty("Content-Type", "application/json");
            initConn.setRequestProperty("Accept", "application/json, text/event-stream");
            initConn.setConnectTimeout(30000);
            initConn.setReadTimeout(30000);
            initConn.setDoOutput(true);
            
            java.io.OutputStreamWriter initWriter = new java.io.OutputStreamWriter(initConn.getOutputStream(), "UTF-8");
            initWriter.write(initBody);
            initWriter.flush();
            initWriter.close();
            
            int initResponseCode = initConn.getResponseCode();
            
            if (initResponseCode != 200) {
                throw new RuntimeException("Initialize请求失败，响应码: " + initResponseCode);
            }
            
            java.io.BufferedReader initReader = new java.io.BufferedReader(
                new java.io.InputStreamReader(initConn.getInputStream(), "UTF-8")
            );
            
            StringBuilder initResponse = new StringBuilder();
            String line;
            while ((line = initReader.readLine()) != null) {
                initResponse.append(line);
            }
            initReader.close();
            
            // 从响应头获取session ID
            List<String> sessionHeaders = initConn.getHeaderFields().get("mcp-session-id");
            if (sessionHeaders != null && !sessionHeaders.isEmpty()) {
                sessionId = sessionHeaders.get(0);
            }
            
            long initTime = System.currentTimeMillis() - initStart;
            System.out.println("[步骤1] Initialize成功，耗时: " + initTime + "ms");
            System.out.println("[步骤1] Session ID: " + (sessionId != null ? sessionId : "未返回"));
            
            // 步骤2: 发送initialized通知
            if (sessionId != null) {
                System.out.println("\n[步骤2] 发送initialized通知...");
                
                Map<String, Object> initializedRequest = Map.of(
                    "jsonrpc", "2.0",
                    "method", "notifications/initialized"
                );
                
                String initializedBody = objectMapper.writeValueAsString(initializedRequest);
                
                java.net.HttpURLConnection initializedConn = (java.net.HttpURLConnection) mcpUrl.openConnection();
                initializedConn.setRequestMethod("POST");
                initializedConn.setRequestProperty("Content-Type", "application/json");
                initializedConn.setRequestProperty("Accept", "application/json, text/event-stream");
                initializedConn.setRequestProperty("mcp-session-id", sessionId);
                initializedConn.setConnectTimeout(30000);
                initializedConn.setReadTimeout(30000);
                initializedConn.setDoOutput(true);
                
                java.io.OutputStreamWriter initializedWriter = new java.io.OutputStreamWriter(initializedConn.getOutputStream(), "UTF-8");
                initializedWriter.write(initializedBody);
                initializedWriter.flush();
                initializedWriter.close();
                
                initializedConn.getResponseCode();
                System.out.println("[步骤2] Initialized通知已发送");
            }
            
            // 步骤3: 发送tools/call请求
            System.out.println("\n[步骤3] 发送tools/call请求...");
            long toolCallStart = System.currentTimeMillis();
            
            Map<String, Object> callRequest = new HashMap<>();
            callRequest.put("jsonrpc", "2.0");
            callRequest.put("id", 2);
            callRequest.put("method", "tools/call");
            callRequest.put("params", Map.of(
                "name", toolName,
                "arguments", arguments != null ? arguments : Map.of()
            ));
            
            String callBody = objectMapper.writeValueAsString(callRequest);
            System.out.println("[步骤3] 请求体: " + callBody);
            
            java.net.HttpURLConnection callConn = (java.net.HttpURLConnection) mcpUrl.openConnection();
            callConn.setRequestMethod("POST");
            callConn.setRequestProperty("Content-Type", "application/json");
            callConn.setRequestProperty("Accept", "application/json, text/event-stream");
            if (sessionId != null) {
                callConn.setRequestProperty("mcp-session-id", sessionId);
            }
            callConn.setConnectTimeout(30000);
            callConn.setReadTimeout(60000); // 60秒超时
            callConn.setDoOutput(true);
            
            java.io.OutputStreamWriter callWriter = new java.io.OutputStreamWriter(callConn.getOutputStream(), "UTF-8");
            callWriter.write(callBody);
            callWriter.flush();
            callWriter.close();
            
            int callResponseCode = callConn.getResponseCode();
            System.out.println("[步骤3] HTTP响应码: " + callResponseCode);
            
            if (callResponseCode != 200) {
                throw new RuntimeException("Tools/call请求失败，响应码: " + callResponseCode);
            }
            
            java.io.BufferedReader callReader = new java.io.BufferedReader(
                new java.io.InputStreamReader(callConn.getInputStream(), "UTF-8")
            );
            
            // 读取SSE响应并提取data部分
            String callJsonData = null;
            while ((line = callReader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    callJsonData = line.substring(5).trim();
                }
            }
            callReader.close();
            
            if (callJsonData == null) {
                throw new RuntimeException("无法从SSE响应中提取data内容");
            }
            
            long toolCallTime = System.currentTimeMillis() - toolCallStart;
            System.out.println("[步骤3] 工具调用完成，耗时: " + toolCallTime + "ms");
            System.out.println("[步骤3] SSE响应内容: " + callJsonData);
            
            // 解析响应
            Map<String, Object> resultMap = new HashMap<>();
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(callJsonData);
            
            if (jsonNode.has("result")) {
                resultMap.put("content", objectMapper.convertValue(jsonNode.get("result"), Object.class));
            } else if (jsonNode.has("error")) {
                resultMap.put("content", jsonNode.get("error").asText());
                resultMap.put("isError", true);
            } else {
                resultMap.put("content", callJsonData);
            }
            
            resultMap.put("success", true);
            
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("\n========================================");
            System.out.println("MCP 工具调用成功");
            System.out.println("========================================");
            System.out.println("总耗时: " + totalTime + "ms");
            System.out.println("  - Initialize: " + initTime + "ms");
            System.out.println("  - Tool Call: " + toolCallTime + "ms");
            System.out.println("========================================\n");
            
            return resultMap;
            
        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            System.err.println("\n========================================");
            System.err.println("MCP 工具调用失败");
            System.err.println("========================================");
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("错误类型: " + e.getClass().getName());
            System.err.println("总耗时: " + errorTime + "ms");
            System.err.println("========================================\n");
            e.printStackTrace();
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("调用MCP工具失败", e);
        }
    }

    /**
     * 关闭 MCP 客户端（在应用关闭时调用）
     * 由于现在每次调用都创建新客户端，此方法不再需要
     */
    public void close() {
            /**
     * println
     * 
     * @author 李吉隆
     */
        System.out.println("[MCP] close()方法已废弃，每次调用都创建新客户端");
    }

    /**
     * 测试MCP连接
     */
    public Map<String, Object> testConnection() {
        McpSyncClient client = null;
        try {
            client = createMcpClient();
            List<Map<String, Object>> tools = listTools();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "MCP连接成功，找到 " + tools.size() + " 个工具");
            result.put("toolCount", tools.size());
            result.put("tools", tools);
            
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "MCP连接失败: " + e.getMessage());
            return result;
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    System.err.println("关闭MCP客户端失败: " + e.getMessage());
                }
            }
        }
    }
}
