package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.service.McpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MCP控制器
 * 提供MCP功能的REST API
 * 
 * @author iFlow
 */
@RestController
@RequestMapping("/mcp")
public class McpController {

    @Autowired
    private McpService mcpService;

    /**
     * 测试MCP连接
     */
    @GetMapping("/test")
    public R testConnection() {
        try {
            Map<String, Object> result = mcpService.testConnection();
            return new R().addData("data", result);
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "MCP连接失败: " + e.getMessage());
            return new R().addData("data", result);
        }
    }

    /**
     * 获取所有工具
     */
    @GetMapping("/tools")
    public R getTools() {
        try {
            List<Map<String, Object>> tools = mcpService.listTools();
            return new R().addData("success", true).addData("tools", tools);
        } catch (Exception e) {
            return new R().addData("success", false).addData("message", "获取工具列表失败: " + e.getMessage());
        }
    }

    /**
     * 调用工具
     */
    @PostMapping("/call")
    public R callTool(@RequestBody Map<String, Object> request) {
        try {
            String toolName = (String) request.get("toolName");
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) request.get("arguments");

            Map<String, Object> result = mcpService.callTool(toolName, arguments);
            
            return new R().addData("success", true).addData("result", result);
        } catch (Exception e) {
            return new R().addData("success", false).addData("message", "调用工具失败: " + e.getMessage());
        }
    }
}