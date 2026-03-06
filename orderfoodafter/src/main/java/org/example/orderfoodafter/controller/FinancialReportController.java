package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.orderfoodafter.entity.FinancialReport;
import org.example.orderfoodafter.service.FinancialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 财务报表Controller
 */
@RestController
@RequestMapping("/financial-report")
public class FinancialReportController {

    @Autowired
    private FinancialReportService financialReportService;

    /**
     * 获取财务报表总览数据
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(@RequestParam(required = false) Long shopId) {
        Map<String, Object> result = financialReportService.getOverviewData(shopId);

        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        response.put("code", 200);
        response.put("message", "查询成功");

        return ResponseEntity.ok(response);
    }

    /**
     * 获取财务统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestParam(required = false) Long shopId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        Map<String, Object> stats = financialReportService.getFinancialStats(shopId, startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("data", stats);
        response.put("code", 200);
        response.put("message", "查询成功");

        return ResponseEntity.ok(response);
    }

    /**
     * 获取财务报表列表(实时计算)
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getReportList(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Byte reportType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        // 如果没有指定日期范围,默认查询最近30天
        if (startDate == null || endDate == null) {
            Calendar cal = Calendar.getInstance();
            endDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            startDate = cal.getTime();
        }

        List<FinancialReport> list = financialReportService.generateDailyReports(shopId, startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("data", list);
        response.put("code", 200);
        response.put("message", "查询成功");

        return ResponseEntity.ok(response);
    }

    /**
     * 生成财务报表
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, Object> params) {
        Long shopId = params.get("shopId") != null ? Long.valueOf(params.get("shopId").toString()) : null;
        Date reportDate = params.get("reportDate") != null ?
                (Date) params.get("reportDate") : new Date();
        Byte reportType = params.get("reportType") != null ?
                Byte.valueOf(params.get("reportType").toString()) : (byte) 1;

        FinancialReport report = financialReportService.generateReport(shopId, reportDate, reportType);

        Map<String, Object> response = new HashMap<>();
        response.put("data", report);
        response.put("code", 200);
        response.put("message", "生成成功");

        return ResponseEntity.ok(response);
    }

    /**
     * 保存财务报表
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveReport(@RequestBody FinancialReport report) {
        boolean result = financialReportService.saveOrUpdate(report);

        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "保存成功");
        } else {
            response.put("code", 500);
            response.put("message", "保存失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取近7天的财务趋势数据
     */
    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getTrend(
            @RequestParam(required = false) Long shopId,
            @RequestParam(defaultValue = "7") Integer days) {

        List<Map<String, Object>> trendData = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        for (int i = days - 1; i >= 0; i--) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            Date date = cal.getTime();

            // 设置日期范围(当天)
            Calendar dayStart = Calendar.getInstance();
            dayStart.setTime(date);
            dayStart.set(Calendar.HOUR_OF_DAY, 0);
            dayStart.set(Calendar.MINUTE, 0);
            dayStart.set(Calendar.SECOND, 0);
            dayStart.set(Calendar.MILLISECOND, 0);

            Calendar dayEnd = Calendar.getInstance();
            dayEnd.setTime(date);
            dayEnd.set(Calendar.HOUR_OF_DAY, 23);
            dayEnd.set(Calendar.MINUTE, 59);
            dayEnd.set(Calendar.SECOND, 59);
            dayEnd.set(Calendar.MILLISECOND, 999);

            Map<String, Object> stats = financialReportService.getFinancialStats(
                    shopId, dayStart.getTime(), dayEnd.getTime());

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", sdf.format(date));
            dayData.put("totalOrders", stats.getOrDefault("totalOrders", 0));
            dayData.put("payAmount", stats.getOrDefault("payAmount", 0));
            dayData.put("totalCustomers", stats.getOrDefault("totalCustomers", 0));

            trendData.add(dayData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", trendData);
        response.put("code", 200);
        response.put("message", "查询成功");

        return ResponseEntity.ok(response);
    }
}