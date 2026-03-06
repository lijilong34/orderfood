package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.SystemStatus;
import org.example.orderfoodafter.service.StatusMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统状态监控控制器
 * 提供系统状态监控API
 */
@RestController
@RequestMapping("/monitor")
public class StatusMonitorController {

    @Autowired
    private StatusMonitorService statusMonitorService;

    /**
     * 获取系统状态
     */
    @GetMapping("/status")
    public R getSystemStatus() {
        R r = new R();
        try {
            SystemStatus status = statusMonitorService.getSystemStatus();
            r.addData("status", status);
            r.setMessage("获取系统状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
            r.setMessage("获取系统状态失败: " + e.getMessage());
        }
        return r;
    }
    
    /**
     * 获取CPU使用率
     */
    @GetMapping("/cpu")
    public R getCpuUsage() {
        R r = new R();
        try {
            SystemStatus status = statusMonitorService.getSystemStatus();
            r.addData("cpuUsage", status.getCpuUsage());
            r.setMessage("获取CPU使用率成功");
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
            r.setMessage("获取CPU使用率失败: " + e.getMessage());
        }
        return r;
    }
    
    /**
     * 获取内存使用情况
     */
    @GetMapping("/memory")
    public R getMemoryUsage() {
        R r = new R();
        try {
            SystemStatus status = statusMonitorService.getSystemStatus();
            r.addData("memory", status.getMemory());
            r.setMessage("获取内存使用情况成功");
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
            r.setMessage("获取内存使用情况失败: " + e.getMessage());
        }
        return r;
    }
    
    /**
     * 获取JVM内存使用情况
     */
    @GetMapping("/jvm")
    public R getJvmMemoryUsage() {
        R r = new R();
        try {
            SystemStatus status = statusMonitorService.getSystemStatus();
            r.addData("jvmMemory", status.getJvmMemory());
            r.setMessage("获取JVM内存使用情况成功");
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
            r.setMessage("获取JVM内存使用情况失败: " + e.getMessage());
        }
        return r;
    }
    
    /**
     * 获取数据库连接池状态
     */
    @GetMapping("/db-pool")
    public R getDbPoolStatus() {
        R r = new R();
        try {
            SystemStatus status = statusMonitorService.getSystemStatus();
            r.addData("dbPool", status.getDbPool());
            r.setMessage("获取数据库连接池状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
            r.setMessage("获取数据库连接池状态失败: " + e.getMessage());
        }
        return r;
    }
}
