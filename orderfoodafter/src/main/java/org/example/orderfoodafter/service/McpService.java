package org.example.orderfoodafter.service;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.*;

/**
 * MCP服务类
 * 使用MCP SDK连接MCP服务器
 * 
 * @author iFlow
 */
@Service
public class McpService {

    @Value("${mcp.server.url}")
    private String mcpServerUrl;

    // 单例 MCP 客户端实例
    private McpSyncClient mcpClient;
    private final Object clientLock = new Object();

    /**
     * 获取或创建 MCP 客户端（单例模式）
     */
    private McpSyncClient getOrCreateMcpClient() {
        synchronized (clientLock) {
            if (mcpClient == null) {
                System.out.println("[MCP] 创建新的 MCP 客户端实例（单例）");
                mcpClient = createMcpClient();
            } else {
                System.out.println("[MCP] 复用现有的 MCP 客户端实例");
            }
            return mcpClient;
        }
    }

    /**
     * 查找 Node.js 可执行文件路径
     */
    private String findNodeExecutable() {
        System.out.println("[MCP] 开始查找 Node.js 可执行文件...");
        
        // 尝试多个可能的 Node.js 路径
        String[] possiblePaths = {
            "node",  // 尝试从 PATH 环境变量查找
            "node.exe",
            "/usr/local/bin/node",
            "/usr/bin/node",
            System.getProperty("user.home") + "/AppData/Roaming/npm/node.cmd",
            System.getProperty("user.home") + "/AppData/Roaming/npm/node.exe",
            "C:\\Program Files\\nodejs\\node.exe",
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
                
                // 尝试通过 where/which 命令查找
                ProcessBuilder pb;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    pb = new ProcessBuilder("cmd", "/c", "where", path);
                } else {
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
     * 查找 MCP Chrome Server 路径
     */
    private String findMcpChromeServer() {
        System.out.println("[MCP] 开始查找 MCP Chrome Server...");
        String userHome = System.getProperty("user.home");
        
        String[] possiblePaths = {
            userHome + "/.config/nvm/versions/node/v24.13.0/lib/node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js",
            userHome + "/AppData/Roaming/npm/node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js",
            userHome + "\\AppData\\Roaming\\npm\\node_modules\\mcp-chrome-bridge\\dist\\mcp\\mcp-server-stdio.js",
            userHome + "/node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js",
            userHome + "\\node_modules\\mcp-chrome-bridge\\dist\\mcp\\mcp-server-stdio.js",
            "./node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js",
            "./node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js",
            "../node_modules/mcp-chrome-bridge/dist/mcp/mcp-server-stdio.js"
        };
        
        for (String path : possiblePaths) {
            System.out.println("[MCP] 尝试路径: " + path);
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                String absolutePath = file.getAbsolutePath();
                System.out.println("[MCP] ✓ 找到 MCP Chrome Server: " + absolutePath);
                return absolutePath;
            } else {
                System.out.println("[MCP] ✗ 文件不存在: " + path);
            }
        }
        
        System.out.println("[MCP] ✗ 未能找到 MCP Chrome Server");
        return null;
    }

    /**
     * 创建新的MCP客户端（每次调用都创建新实例）
     */
    private McpSyncClient createMcpClient() {
        int maxRetries = 3;
        int retryCount = 0;
        McpSyncClient client = null;
        
        while (retryCount < maxRetries) {
            try {
                System.out.println("========================================");
                System.out.println("创建MCP客户端");
                System.out.println("========================================");
                System.out.println("尝试 " + (retryCount + 1) + "/" + maxRetries);
                System.out.println("时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()));

                // 自动查找 Node.js 路径（支持 Windows 和 Linux）
                String nodePath = findNodeExecutable();
                String mcpChromeStdioPath = findMcpChromeServer();
                
                System.out.println("\n[步骤1] 查找 Node.js 和 MCP Server...");
                if (nodePath == null) {
                    throw new RuntimeException("无法找到 Node.js，请确保已正确安装");
                }
                if (mcpChromeStdioPath == null) {
                    throw new RuntimeException("无法找到 MCP Chrome Server，请确保已正确安装");
                }
                
                System.out.println("  Node.js 路径: " + nodePath);
                System.out.println("  MCP Server 路径: " + mcpChromeStdioPath);
                
                // 检查可执行文件是否存在
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
                
                // 添加环境变量来隔离连接状态
                Map<String, String> environment = new HashMap<>();
                environment.put("NODE_ENV", "production");
                environment.put("MCP_SESSION_ID", String.valueOf(System.currentTimeMillis()));
                environment.put("MCP_PORT", String.valueOf(9000 + (System.currentTimeMillis() % 1000))); // 随机端口
                
                System.out.println("\n[步骤2] 构建服务器参数...");
                ServerParameters serverParams = ServerParameters.builder(nodePath)
                    .args(List.of(mcpChromeStdioPath))
                    .env(environment)
                    .build();
                
                ObjectMapper objectMapper = new ObjectMapper();
                JacksonMcpJsonMapper jsonMapper = new JacksonMcpJsonMapper(objectMapper);
                StdioClientTransport transport = new StdioClientTransport(serverParams, jsonMapper);

                // 创建同步客户端，设置 120 秒超时
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
                
                // 成功则退出重试循环
                break;
                
            } catch (Exception e) {
                retryCount++;
                System.err.println("\n========================================");
                System.err.println("创建MCP客户端失败");
                System.err.println("========================================");
                System.err.println("尝试 " + retryCount + "/" + maxRetries);
                System.err.println("错误信息: " + e.getMessage());
                System.err.println("错误类型: " + e.getClass().getName());
                System.err.println("========================================\n");
                e.printStackTrace();
                
                // 清理失败的客户端实例
                if (client != null) {
                    try {
                        client.close();
                    } catch (Exception closeEx) {
                        System.err.println("关闭失败的MCP客户端时出错: " + closeEx.getMessage());
                    }
                    client = null;
                }
                
                // 如果达到最大重试次数，抛出异常
                if (retryCount >= maxRetries) {
                    throw new RuntimeException("创建MCP客户端失败，已重试 " + maxRetries + " 次: " + e.getMessage(), e);
                }
                
                // 等待一段时间后重试
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
     * 列出所有可用的MCP工具
     */
    public List<Map<String, Object>> listTools() {
        System.out.println("========================================");
        System.out.println("获取 MCP 工具列表");
        System.out.println("========================================");
        System.out.println("调用时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()));
        
        long startTime = System.currentTimeMillis();
        McpSyncClient client = null;
        
        try {
            // 使用单例客户端
            System.out.println("\n[步骤1] 获取 MCP 客户端...");
            client = getOrCreateMcpClient();
            long clientCreateTime = System.currentTimeMillis() - startTime;
            System.out.println("[步骤1] MCP 客户端获取成功，耗时: " + clientCreateTime + "ms");
            
            System.out.println("\n[步骤2] 获取工具列表...");
            long listStart = System.currentTimeMillis();
            var result = client.listTools();
            long listTime = System.currentTimeMillis() - listStart;
            System.out.println("[步骤2] 工具列表获取成功，耗时: " + listTime + "ms");
            
            List<Map<String, Object>> tools = new ArrayList<>();
            System.out.println("\n[步骤3] 解析工具信息...");
            System.out.println("找到 " + result.tools().size() + " 个可用工具:");
            
            int index = 1;
            for (var tool : result.tools()) {
                Map<String, Object> toolMap = new HashMap<>();
                toolMap.put("name", tool.name());
                
                System.out.println("\n  工具 #" + index + ":");
                System.out.println("    名称: " + tool.name());
                
                if (tool.description() != null) {
                    toolMap.put("description", tool.description());
                    System.out.println("    描述: " + tool.description());
                } else {
                    System.out.println("    描述: 无");
                }
                
                if (tool.inputSchema() != null) {
                    toolMap.put("inputSchema", tool.inputSchema());
                    System.out.println("    输入模式: " + tool.inputSchema());
                } else {
                    System.out.println("    输入模式: 无");
                }
                
                tools.add(toolMap);
                index++;
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("\n========================================");
            System.out.println("MCP 工具列表获取成功");
            System.out.println("========================================");
            System.out.println("总耗时: " + totalTime + "ms");
            System.out.println("  - 客户端获取: " + clientCreateTime + "ms");
            System.out.println("  - 列表获取: " + listTime + "ms");
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
            throw new RuntimeException("获取MCP工具列表失败", e);
        }
    }

    /**
     * 调用指定的MCP工具
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
                System.out.println("  " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(arguments));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                System.out.println("  [参数序列化失败] " + arguments);
            }
        } else {
            System.out.println("工具参数: 无");
        }
        
        long startTime = System.currentTimeMillis();
        McpSyncClient client = null;
        
        try {
            // 每次调用都创建新的客户端实例，避免连接状态问题
            System.out.println("\n[步骤1] 创建 MCP 客户端...");
            client = createMcpClient();
            long clientCreateTime = System.currentTimeMillis() - startTime;
            System.out.println("[步骤1] MCP 客户端创建成功，耗时: " + clientCreateTime + "ms");
            
            System.out.println("\n[步骤2] 构建工具调用请求...");
            var request = McpSchema.CallToolRequest.builder()
                .name(toolName)
                .arguments(arguments != null ? arguments : Map.of())
                .build();
            System.out.println("[步骤2] 请求构建完成");
            
            System.out.println("\n[步骤3] 调用 MCP 工具...");
            long toolCallStart = System.currentTimeMillis();
            var result = client.callTool(request);
            long toolCallTime = System.currentTimeMillis() - toolCallStart;
            System.out.println("[步骤3] MCP 工具调用完成，耗时: " + toolCallTime + "ms");
            
            System.out.println("\n[步骤4] 处理返回结果...");
            Map<String, Object> resultMap = new HashMap<>();
            if (result.content() != null) {
                resultMap.put("content", result.content());
                System.out.println("返回内容类型: " + result.content().getClass().getSimpleName());
                System.out.println("返回内容大小: " + result.content().size() + " 项");
                
                if (!result.content().isEmpty()) {
                    Object firstContent = result.content().get(0);
                    System.out.println("第一项内容类型: " + firstContent.getClass().getSimpleName());
                }
            }
            if (result.isError() != null) {
                resultMap.put("isError", result.isError());
                System.out.println("是否错误: " + result.isError());
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("\n========================================");
            System.out.println("MCP 工具调用成功");
            System.out.println("========================================");
            System.out.println("总耗时: " + totalTime + "ms");
            System.out.println("  - 客户端创建: " + clientCreateTime + "ms");
            System.out.println("  - 工具调用: " + toolCallTime + "ms");
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
            
            // 返回错误信息，而不是抛出异常
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("isError", true);
            errorMap.put("content", List.of("工具执行失败: " + e.getMessage()));
            return errorMap;
        } finally {
            // 无论成功还是失败，都关闭客户端
            if (client != null) {
                try {
                    System.out.println("[MCP] 关闭 MCP 客户端实例");
                    client.close();
                } catch (Exception closeEx) {
                    System.err.println("关闭MCP客户端失败: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * 关闭 MCP 客户端（在应用关闭时调用）
     */
    public void close() {
        synchronized (clientLock) {
            if (mcpClient != null) {
                try {
                    System.out.println("[MCP] 关闭 MCP 客户端实例");
                    mcpClient.close();
                    mcpClient = null;
                } catch (Exception e) {
                    System.err.println("关闭MCP客户端失败: " + e.getMessage());
                }
            }
        }
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