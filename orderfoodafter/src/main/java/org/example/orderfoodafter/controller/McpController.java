// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入通用返回结果类R
import org.example.orderfoodafter.common.R;
// 导入MCP服务类
import org.example.orderfoodafter.service.McpService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Web的绑定注解
import org.springframework.web.bind.annotation.*;

// 导入Java集合框架的List接口
import java.util.List;
// 导入Java集合框架的Map接口
import java.util.Map;

/**
 * MCP控制器
 * 提供MCP（Model Context Protocol）功能的REST API，支持工具调用和连接测试
 * 
 * @author 周子金
 * @date 2026-02-28
 */
// 使用RestController注解标识这是一个REST控制器
@RestController
// 设置请求映射路径为/mcp
@RequestMapping("/mcp")
// 定义McpController控制器类
/**
 * McpController类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class McpController {

    // 使用Autowired注解自动注入McpService服务
    @Autowired
    // 声明McpService服务实例
    private McpService mcpService;

    /**
     * 测试MCP连接
     * 作者:周子金
     * @return 连接测试结果
     */
    // 使用GetMapping注解映射GET请求到/test路径
    @GetMapping("/test")
    // 定义测试连接的方法，返回R类型对象
        /**
     * testConnection
     * 
     * @author 李吉隆
     */
    public R testConnection() {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层的测试连接方法，获取测试结果
            Map<String, Object> result = mcpService.testConnection();
            // 返回成功结果，将测试结果添加到响应数据中
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("data", result);
        } catch (Exception e) {
            // 捕获异常，创建失败结果的Map对象
            Map<String, Object> result = new java.util.HashMap<>();
            // 设置success为false
            result.put("success", false);
            // 设置失败消息
            result.put("message", "MCP连接失败: " + e.getMessage());
            // 返回失败结果
                /**
     * R
     * 
     * @author 李吉隆
     */
            return new R().addData("data", result);
        }
    }

    /**
     * 获取所有工具
     * 作者:周子金
     * @return 工具列表
     */
    // 使用GetMapping注解映射GET请求到/tools路径
    @GetMapping("/tools")
    // 定义获取工具列表的方法，返回R类型对象
        /**
     * 获取 getTools
     * 
     * @return getTools
     * @author 李吉隆
     */
    public R getTools() {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层获取工具列表
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
     * 调用工具
     * 作者:周子金
     * @param request 请求参数（包含工具名称和参数）
     * @return 工具调用结果
     */
    // 使用PostMapping注解映射POST请求到/call路径
    @PostMapping("/call")
    // 定义调用工具的方法，接收请求体参数，返回R类型对象
        /**
     * callTool
     * 
     * @author 李吉隆
     */
    public R callTool(@RequestBody Map<String, Object> request) {
        // 使用try-catch块处理可能的异常
        try {
            // 从请求参数中获取工具名称
            String toolName = (String) request.get("toolName");
            // 抑制类型转换警告
            @SuppressWarnings("unchecked")
            // 从请求参数中获取工具参数，类型转换为Map
            Map<String, Object> arguments = (Map<String, Object>) request.get("arguments");

            // 调用服务层执行工具调用
            Map<String, Object> result = mcpService.callTool(toolName, arguments);

            // 返回成功结果，包含工具调用结果
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
            return new R().addData("success", false).addData("message", "调用工具失败: " + e.getMessage());
        }
    }
}