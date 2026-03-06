package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.OperationLog;
import org.example.orderfoodafter.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 操作日志Controller
 * 提供日志管理的API接口
 */
@RestController
@RequestMapping("/log")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 记录操作日志
     * @param operationLog 操作日志对象
     * @return 操作结果
     */
    @PostMapping("/record")
    public R recordOperationLog(@RequestBody OperationLog operationLog) {
        try {
            int result = operationLogService.recordOperationLog(operationLog);
            if (result > 0) {
                return R.ok().addData("message", "日志记录成功");
            } else {
                return R.error("日志记录失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("日志记录异常: " + e.getMessage());
        }
    }

    /**
     * 分页查询操作日志列表
     * @param params 查询条件
     * @return 操作日志列表
     */
    @GetMapping("/list")
    public R getOperationLogList(@RequestParam Map<String, Object> params) {
        try {
            // 获取分页参数
            int pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

            // 查询日志列表
            Map<String, Object> result = operationLogService.getOperationLogList(params, pageNum, pageSize);
            return R.ok().addData("logList", result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询日志列表异常: " + e.getMessage());
        }
    }

    /**
     * 根据日志ID查询操作日志
     * @param logId 日志ID
     * @return 操作日志对象
     */
    @GetMapping("/getById/{logId}")
    public R getOperationLogById(@PathVariable Long logId) {
        try {
            OperationLog operationLog = operationLogService.getOperationLogById(logId);
            return R.ok().addData("log", operationLog);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询日志详情异常: " + e.getMessage());
        }
    }

    /**
     * 删除操作日志
     * @param logId 日志ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{logId}")
    public R deleteOperationLog(@PathVariable Long logId) {
        try {
            int result = operationLogService.deleteOperationLog(logId);
            if (result > 0) {
                return R.ok().addData("message", "日志删除成功");
            } else {
                return R.error("日志删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("删除日志异常: " + e.getMessage());
        }
    }

    /**
     * 批量删除操作日志
     * @param logIds 日志ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batchDelete")
    public R batchDeleteOperationLog(@RequestBody List<Long> logIds) {
        try {
            int result = operationLogService.batchDeleteOperationLog(logIds);
            if (result > 0) {
                return R.ok().addData("message", "日志批量删除成功");
            } else {
                return R.error("日志批量删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("批量删除日志异常: " + e.getMessage());
        }
    }

    /**
     * 根据时间范围删除操作日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作结果
     */
    @DeleteMapping("/deleteByTimeRange")
    public R deleteOperationLogByTimeRange(@RequestParam String startTime, @RequestParam String endTime) {
        try {
            int result = operationLogService.deleteOperationLogByTimeRange(startTime, endTime);
            if (result > 0) {
                return R.ok().addData("message", "按时间范围删除日志成功");
            } else {
                return R.error("按时间范围删除日志失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("按时间范围删除日志异常: " + e.getMessage());
        }
    }
}