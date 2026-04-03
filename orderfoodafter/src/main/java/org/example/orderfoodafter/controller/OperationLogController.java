// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入通用返回结果类R
import org.example.orderfoodafter.common.R;
// 导入操作日志实体类
import org.example.orderfoodafter.entity.OperationLog;
// 导入操作日志服务类
import org.example.orderfoodafter.service.OperationLogService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Web的绑定注解
import org.springframework.web.bind.annotation.*;

// 导入Java集合框架的List接口
import java.util.List;
// 导入Java集合框架的Map接口
import java.util.Map;

/**
 * 操作日志控制器
 * 负责管理系统操作日志的记录、查询、删除等功能，用于审计和追踪用户操作行为
 * 
 * @author 周子金
 * @date 2026-01-20
 */
// 使用RestController注解标识这是一个REST控制器
@RestController
// 设置请求映射路径为/log
@RequestMapping("/log")
// 定义OperationLogController控制器类
/**
 * OperationLogController类
 *
 * @author 周子金
 * @date 2026-03-18
 */

public class OperationLogController {

    // 使用Autowired注解自动注入OperationLogService服务
    @Autowired
    // 声明OperationLogService服务实例
    private OperationLogService operationLogService;

    /**
     * 记录操作日志
     * @param operationLog 操作日志对象
     * @return 操作结果
     */
    // 使用PostMapping注解映射POST请求到/record路径
    @PostMapping("/record")
    // 定义记录操作日志的方法，接收请求体参数，返回R类型对象
        /**
     * recordOperationLog
     * 
     * @author 周子金
     */
    public R recordOperationLog(@RequestBody OperationLog operationLog) {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层记录操作日志，返回受影响的行数
            int result = operationLogService.recordOperationLog(operationLog);
            // 判断记录结果
            if (result > 0) {
                // 记录成功，返回成功消息
                return R.ok().addData("message", "日志记录成功");
            } else {
                // 记录失败，返回错误消息
                return R.error("日志记录失败");
            }
        } catch (Exception e) {
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 返回错误消息
            return R.error("日志记录异常: " + e.getMessage());
        }
    }

    /**
     * 分页查询操作日志列表
     * 作者:熊杨博
     * @param params 查询条件
     * @return 操作日志列表
     */
    // 使用GetMapping注解映射GET请求到/list路径
    @GetMapping("/list")
    // 定义查询操作日志列表的方法，接收请求参数，返回R类型对象
        /**
     * 获取 getOperationLogList
     * 
     * @return getOperationLogList
     * @author 周子金
     */
    public R getOperationLogList(@RequestParam Map<String, Object> params) {
        // 使用try-catch块处理可能的异常
        try {
            // 获取分页参数
            int pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

            // 查询日志列表
            Map<String, Object> result = operationLogService.getOperationLogList(params, pageNum, pageSize);
            // 返回查询结果
            return R.ok().addData("logList", result);
        } catch (Exception e) {
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 返回错误消息
            return R.error("查询日志列表异常: " + e.getMessage());
        }
    }

    /**
     * 根据日志ID查询操作日志
     * 作者:熊杨博
     * @param logId 日志ID
     * @return 操作日志对象
     */
    // 使用GetMapping注解映射GET请求到/getById/{logId}路径
    @GetMapping("/getById/{logId}")
    // 定义根据ID查询操作日志的方法，从路径变量获取日志ID，返回R类型对象
/**
 * getOperationLogById方法
 *
 * @author 周子金
 */
    public R getOperationLogById(@PathVariable Long logId) {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层根据ID查询操作日志
            OperationLog operationLog = operationLogService.getOperationLogById(logId);
            // 返回查询结果
            return R.ok().addData("log", operationLog);
        } catch (Exception e) {
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 返回错误消息
            return R.error("查询日志详情异常: " + e.getMessage());
        }
    }

    /**
     * 删除操作日志
     * 作者:熊杨博
     * @param logId 日志ID
     * @return 操作结果
     */
    // 使用DeleteMapping注解映射DELETE请求到/delete/{logId}路径
    @DeleteMapping("/delete/{logId}")
    // 定义删除操作日志的方法，从路径变量获取日志ID，返回R类型对象
/**
 * deleteOperationLog方法
 *
 * @author 周子金
 */
    public R deleteOperationLog(@PathVariable Long logId) {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层删除操作日志，返回受影响的行数
            int result = operationLogService.deleteOperationLog(logId);
            // 判断删除结果
            if (result > 0) {
                // 删除成功，返回成功消息
                return R.ok().addData("message", "日志删除成功");
            } else {
                // 删除失败，返回错误消息
                return R.error("日志删除失败");
            }
        } catch (Exception e) {
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 返回错误消息
            return R.error("删除日志异常: " + e.getMessage());
        }
    }

    /**
     * 批量删除操作日志
     * 作者:熊杨博
     * @param logIds 日志ID列表
     * @return 操作结果
     */
    // 使用DeleteMapping注解映射DELETE请求到/batchDelete路径
    @DeleteMapping("/batchDelete")
    // 定义批量删除操作日志的方法，接收请求体参数，返回R类型对象
        /**
     * batchDeleteOperationLog
     * 
     * @author 周子金
     */
    public R batchDeleteOperationLog(@RequestBody List<Long> logIds) {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层批量删除操作日志，返回受影响的行数
            int result = operationLogService.batchDeleteOperationLog(logIds);
            // 判断删除结果
            if (result > 0) {
                // 删除成功，返回成功消息
                return R.ok().addData("message", "日志批量删除成功");
            } else {
                // 删除失败，返回错误消息
                return R.error("日志批量删除失败");
            }
        } catch (Exception e) {
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 返回错误消息
            return R.error("批量删除日志异常: " + e.getMessage());
        }
    }

    /**
     * 根据时间范围删除操作日志
     * 作者:熊杨博
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作结果
     */
    // 使用DeleteMapping注解映射DELETE请求到/deleteByTimeRange路径
    @DeleteMapping("/deleteByTimeRange")
    // 定义根据时间范围删除操作日志的方法，接收请求参数，返回R类型对象
        /**
     * deleteOperationLogByTimeRange
     * 
     * @author 周子金
     */
    public R deleteOperationLogByTimeRange(@RequestParam String startTime, @RequestParam String endTime) {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层根据时间范围删除操作日志，返回受影响的行数
            int result = operationLogService.deleteOperationLogByTimeRange(startTime, endTime);
            // 判断删除结果
            if (result > 0) {
                // 删除成功，返回成功消息
                return R.ok().addData("message", "按时间范围删除日志成功");
            } else {
                // 删除失败，返回错误消息
                return R.error("按时间范围删除日志失败");
            }
        } catch (Exception e) {
            // 捕获异常，打印异常堆栈
            e.printStackTrace();
            // 返回错误消息
            return R.error("按时间范围删除日志异常: " + e.getMessage());
        }
    }
}