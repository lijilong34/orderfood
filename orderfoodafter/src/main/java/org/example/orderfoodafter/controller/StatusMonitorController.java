// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入SystemStatus实体类
import org.example.orderfoodafter.entity.SystemStatus;
// 导入StatusMonitorService服务接口
import org.example.orderfoodafter.service.StatusMonitorService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统状态监控控制器
 * 负责监控系统运行状态，包括CPU使用率、内存使用情况、JVM状态、数据库连接池状态等
 *
 * @author 李吉隆
 * @date 2025-11-30
 */
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 使用RequestMapping注解设置该控制器的基础请求路径为/monitor
@RequestMapping("/monitor")
// 定义StatusMonitorController类
/**
 * StatusMonitorController类
 *
 * @author 李吉隆
 * @date 2026-03-18
 */

public class StatusMonitorController {

    // 使用Autowired注解自动注入StatusMonitorService服务实例
    @Autowired
    // 声明StatusMonitorService服务对象
    private StatusMonitorService statusMonitorService;

    /**
     * 获取系统状态
     */
    // 使用GetMapping注解映射GET请求到/status路径
    @GetMapping("/status")
    // 定义获取系统状态的方法，返回R响应对象
        /**
     * 获取 getSystemStatus
     * 
     * @return getSystemStatus
     * @author 李吉隆
     */
    public R getSystemStatus() {
        // 创建响应对象
            /**
     * R
     * 
     * @author 李吉隆
     */
        R r = new R();
        // 使用try-catch块捕获异常
        try {
            // 调用service层获取系统状态
            SystemStatus status = statusMonitorService.getSystemStatus();
            // 将系统状态添加到响应数据中
            r.addData("status", status);
            // 设置响应消息为成功
            r.setMessage("获取系统状态成功");
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码为500
            r.setCode(500);
            // 设置响应消息为失败并包含错误信息
            r.setMessage("获取系统状态失败: " + e.getMessage());
        }
        // 返回响应对象
        return r;
    }

    /**
     * 获取CPU使用率
     */
    // 使用GetMapping注解映射GET请求到/cpu路径
    @GetMapping("/cpu")
    // 定义获取CPU使用率的方法，返回R响应对象
        /**
     * 获取 getCpuUsage
     * 
     * @return getCpuUsage
     * @author 李吉隆
     */
    public R getCpuUsage() {
        // 创建响应对象
            /**
     * R
     * 
     * @author 李吉隆
     */
        R r = new R();
        // 使用try-catch块捕获异常
        try {
            // 调用service层获取系统状态
            SystemStatus status = statusMonitorService.getSystemStatus();
            // 将CPU使用率添加到响应数据中
            r.addData("cpuUsage", status.getCpuUsage());
            // 设置响应消息为成功
            r.setMessage("获取CPU使用率成功");
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码为500
            r.setCode(500);
            // 设置响应消息为失败并包含错误信息
            r.setMessage("获取CPU使用率失败: " + e.getMessage());
        }
        // 返回响应对象
        return r;
    }

    /**
     * 获取内存使用情况
     */
    // 使用GetMapping注解映射GET请求到/memory路径
    @GetMapping("/memory")
    // 定义获取内存使用情况的方法，返回R响应对象
        /**
     * 获取 getMemoryUsage
     * 
     * @return getMemoryUsage
     * @author 李吉隆
     */
    public R getMemoryUsage() {
        // 创建响应对象
            /**
     * R
     * 
     * @author 李吉隆
     */
        R r = new R();
        // 使用try-catch块捕获异常
        try {
            // 调用service层获取系统状态
            SystemStatus status = statusMonitorService.getSystemStatus();
            // 将内存使用情况添加到响应数据中
            r.addData("memory", status.getMemory());
            // 设置响应消息为成功
            r.setMessage("获取内存使用情况成功");
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码为500
            r.setCode(500);
            // 设置响应消息为失败并包含错误信息
            r.setMessage("获取内存使用情况失败: " + e.getMessage());
        }
        // 返回响应对象
        return r;
    }

    /**
     * 获取JVM内存使用情况
     */
    // 使用GetMapping注解映射GET请求到/jvm路径
    @GetMapping("/jvm")
    // 定义获取JVM内存使用情况的方法，返回R响应对象
        /**
     * 获取 getJvmMemoryUsage
     * 
     * @return getJvmMemoryUsage
     * @author 李吉隆
     */
    public R getJvmMemoryUsage() {
        // 创建响应对象
            /**
     * R
     * 
     * @author 李吉隆
     */
        R r = new R();
        // 使用try-catch块捕获异常
        try {
            // 调用service层获取系统状态
            SystemStatus status = statusMonitorService.getSystemStatus();
            // 将JVM内存使用情况添加到响应数据中
            r.addData("jvmMemory", status.getJvmMemory());
            // 设置响应消息为成功
            r.setMessage("获取JVM内存使用情况成功");
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码为500
            r.setCode(500);
            // 设置响应消息为失败并包含错误信息
            r.setMessage("获取JVM内存使用情况失败: " + e.getMessage());
        }
        // 返回响应对象
        return r;
    }

    /**
     * 获取数据库连接池状态
     */
    // 使用GetMapping注解映射GET请求到/db-pool路径
    @GetMapping("/db-pool")
    // 定义获取数据库连接池状态的方法，返回R响应对象
        /**
     * 获取 getDbPoolStatus
     * 
     * @return getDbPoolStatus
     * @author 李吉隆
     */
    public R getDbPoolStatus() {
        // 创建响应对象
            /**
     * R
     * 
     * @author 李吉隆
     */
        R r = new R();
        // 使用try-catch块捕获异常
        try {
            // 调用service层获取系统状态
            SystemStatus status = statusMonitorService.getSystemStatus();
            // 将数据库连接池状态添加到响应数据中
            r.addData("dbPool", status.getDbPool());
            // 设置响应消息为成功
            r.setMessage("获取数据库连接池状态成功");
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 设置响应状态码为500
            r.setCode(500);
            // 设置响应消息为失败并包含错误信息
            r.setMessage("获取数据库连接池状态失败: " + e.getMessage());
        }
        // 返回响应对象
        return r;
    }
}