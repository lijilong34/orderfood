// 定义财务报表控制器的包路径
package org.example.orderfoodafter.controller;
// 导入MyBatis Plus的Lambda查询条件包装器类
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// 导入财务报表实体类
import org.example.orderfoodafter.entity.FinancialReport;
// 导入财务报表服务接口
import org.example.orderfoodafter.service.FinancialReportService;
// 导入Spring的自动装配注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的日期格式化注解
import org.springframework.format.annotation.DateTimeFormat;
// 导入Spring的响应实体类
import org.springframework.http.ResponseEntity;
// 导入Spring的请求映射注解集合
import org.springframework.web.bind.annotation.*;
// 导入Java的SimpleDateFormat日期格式化类
import java.text.SimpleDateFormat;
// 导入Java的Util包下的所有类
import java.util.*;

/**
 * 财务报表控制器
 * 负责生成和管理财务报表，包括财务总览、统计数据、报表生成、趋势分析等功能
 * 
 * @author 熊杨博
 * @date 2026-01-15
 */
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义该控制器的请求路径前缀为/financial-report
@RequestMapping("/financial-report")
// 定义财务报表控制器类
/**
 * FinancialReportController类
 *
 * @author 熊杨博
 * @date 2026-03-18
 */

public class FinancialReportController {

    // 自动注入财务报表服务，使用依赖注入方式获取实例
    @Autowired
    private FinancialReportService financialReportService;

    /**
     * 获取财务报表总览数据
     */
    // 定义处理获取财务报表总览的GET请求接口，路径为/overview
    @GetMapping("/overview")
    // 定义获取财务报表总览的方法，接收可选的店铺ID参数，返回响应实体对象
    public ResponseEntity<Map<String, Object>> getOverview(@RequestParam(required = false) Long shopId) {
        // 调用服务获取财务报表总览数据
        Map<String, Object> result = financialReportService.getOverviewData(shopId);

        // 创建响应Map对象
        Map<String, Object> response = new HashMap<>();
        // 将总览数据放入响应Map
        response.put("data", result);
        // 设置响应状态码为200
        response.put("code", 200);
        // 设置响应消息为查询成功
        response.put("message", "查询成功");

        // 返回成功响应
        return ResponseEntity.ok(response);
    }

    /**
     * 获取财务统计数据
     */
    // 定义处理获取财务统计的GET请求接口，路径为/stats
    @GetMapping("/stats")
    // 定义获取财务统计的方法，接收可选的店铺ID、开始日期和结束日期参数，返回响应实体对象
    public ResponseEntity<Map<String, Object>> getStats(
            // 接收可选的店铺ID参数
            @RequestParam(required = false) Long shopId,
            // 接收开始日期参数，格式为yyyy-MM-dd
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            // 接收结束日期参数，格式为yyyy-MM-dd
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        // 调用服务获取财务统计数据
        Map<String, Object> stats = financialReportService.getFinancialStats(shopId, startDate, endDate);

        // 创建响应Map对象
        Map<String, Object> response = new HashMap<>();
        // 将统计数据放入响应Map
        response.put("data", stats);
        // 设置响应状态码为200
        response.put("code", 200);
        // 设置响应消息为查询成功
        response.put("message", "查询成功");

        // 返回成功响应
        return ResponseEntity.ok(response);
    }

    /**
     * 获取财务报表列表(实时计算)
     */
    // 定义处理获取财务报表列表的GET请求接口，路径为/list
    @GetMapping("/list")
    // 定义获取财务报表列表的方法，接收可选的店铺ID、报表类型、开始日期和结束日期参数，返回响应实体对象
    public ResponseEntity<Map<String, Object>> getReportList(
            // 接收可选的店铺ID参数
            @RequestParam(required = false) Long shopId,
            // 接收可选的报表类型参数
            @RequestParam(required = false) Byte reportType,
            // 接收可选的开始日期参数，格式为yyyy-MM-dd
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            // 接收可选的结束日期参数，格式为yyyy-MM-dd
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        // 如果没有指定日期范围,默认查询最近30天
        if (startDate == null || endDate == null) {
            // 获取当前日历对象
            Calendar cal = Calendar.getInstance();
            // 设置结束日期为当前时间
            endDate = cal.getTime();
            // 将开始日期设置为30天前
            cal.add(Calendar.DAY_OF_MONTH, -30);
            startDate = cal.getTime();
        }

        // 调用服务生成日报表列表
        List<FinancialReport> list = financialReportService.generateDailyReports(shopId, startDate, endDate);

        // 创建响应Map对象
        Map<String, Object> response = new HashMap<>();
        // 将报表列表放入响应Map
        response.put("data", list);
        // 设置响应状态码为200
        response.put("code", 200);
        // 设置响应消息为查询成功
        response.put("message", "查询成功");

        // 返回成功响应
        return ResponseEntity.ok(response);
    }

    /**
     * 生成财务报表
     */
    // 定义处理生成财务报表的POST请求接口，路径为/generate
    @PostMapping("/generate")
    // 定义生成财务报表的方法，接收包含报表参数的Map对象，返回响应实体对象
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, Object> params) {
        // 从参数中获取店铺ID，如果不存在则为null
        Long shopId = params.get("shopId") != null ? Long.valueOf(params.get("shopId").toString()) : null;
        // 从参数中获取报表日期，如果不存在则为当前日期
        Date reportDate = params.get("reportDate") != null ?
                    /**
     * 获取 get
     * 
     * @return get
     * @author 熊杨博
     */
                (Date) params.get("reportDate") : new Date();
        // 从参数中获取报表类型，如果不存在则为1
        Byte reportType = params.get("reportType") != null ?
                Byte.valueOf(params.get("reportType").toString()) : (byte) 1;

        // 调用服务生成财务报表
        FinancialReport report = financialReportService.generateReport(shopId, reportDate, reportType);

        // 创建响应Map对象
        Map<String, Object> response = new HashMap<>();
        // 将报表数据放入响应Map
        response.put("data", report);
        // 设置响应状态码为200
        response.put("code", 200);
        // 设置响应消息为生成成功
        response.put("message", "生成成功");

        // 返回成功响应
        return ResponseEntity.ok(response);
    }

    /**
     * 保存财务报表
     */
    // 定义处理保存财务报表的POST请求接口，路径为/save
    @PostMapping("/save")
    // 定义保存财务报表的方法，接收财务报表实体对象，返回响应实体对象
    public ResponseEntity<Map<String, Object>> saveReport(@RequestBody FinancialReport report) {
        // 调用服务保存或更新财务报表
        boolean result = financialReportService.saveOrUpdate(report);

        // 创建响应Map对象
        Map<String, Object> response = new HashMap<>();
        // 判断保存是否成功
        if (result) {
            // 如果保存成功，设置响应状态码为200
            response.put("code", 200);
            // 设置响应消息为保存成功
            response.put("message", "保存成功");
        } else {
            // 如果保存失败，设置响应状态码为500
            response.put("code", 500);
            // 设置响应消息为保存失败
            response.put("message", "保存失败");
        }

        // 返回响应
        return ResponseEntity.ok(response);
    }

    /**
     * 获取近7天的财务趋势数据
     */
    // 定义处理获取财务趋势的GET请求接口，路径为/trend
    @GetMapping("/trend")
    // 定义获取财务趋势的方法，接收可选的店铺ID和天数参数，返回响应实体对象
    public ResponseEntity<Map<String, Object>> getTrend(
            // 接收可选的店铺ID参数
            @RequestParam(required = false) Long shopId,
            // 接收天数参数，默认为7天
            @RequestParam(defaultValue = "7") Integer days) {

        // 创建趋势数据列表
        List<Map<String, Object>> trendData = new ArrayList<>();
        // 创建日期格式化对象
            /**
     * SimpleDateFormat
     * 
     * @author 熊杨博
     */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 获取当前日历对象
        Calendar cal = Calendar.getInstance();
        // 循环处理每一天的数据
        for (int i = days - 1; i >= 0; i--) {
            // 将日期设置为i天前
            cal.add(Calendar.DAY_OF_MONTH, -1);
            // 获取当前日期
            Date date = cal.getTime();

            // 设置日期范围(当天)
            // 创建当天的开始时间日历对象
            Calendar dayStart = Calendar.getInstance();
            // 设置开始时间
            dayStart.setTime(date);
            // 设置小时为0
            dayStart.set(Calendar.HOUR_OF_DAY, 0);
            // 设置分钟为0
            dayStart.set(Calendar.MINUTE, 0);
            // 设置秒为0
            dayStart.set(Calendar.SECOND, 0);
            // 设置毫秒为0
            dayStart.set(Calendar.MILLISECOND, 0);

            // 创建当天的结束时间日历对象
            Calendar dayEnd = Calendar.getInstance();
            // 设置结束时间
            dayEnd.setTime(date);
            // 设置小时为23
            dayEnd.set(Calendar.HOUR_OF_DAY, 23);
            // 设置分钟为59
            dayEnd.set(Calendar.MINUTE, 59);
            // 设置秒为59
            dayEnd.set(Calendar.SECOND, 59);
            // 设置毫秒为999
            dayEnd.set(Calendar.MILLISECOND, 999);

            // 调用服务获取当天的财务统计数据
            Map<String, Object> stats = financialReportService.getFinancialStats(
                    shopId, dayStart.getTime(), dayEnd.getTime());

            // 创建当天数据Map对象
            Map<String, Object> dayData = new HashMap<>();
            // 将日期字符串放入当天数据Map
            dayData.put("date", sdf.format(date));
            // 将订单总数放入当天数据Map，默认为0
            dayData.put("totalOrders", stats.getOrDefault("totalOrders", 0));
            // 将支付金额放入当天数据Map，默认为0
            dayData.put("payAmount", stats.getOrDefault("payAmount", 0));
            // 将客户总数放入当天数据Map，默认为0
            dayData.put("totalCustomers", stats.getOrDefault("totalCustomers", 0));

            // 将当天数据添加到趋势数据列表
            trendData.add(dayData);
        }

        // 创建响应Map对象
        Map<String, Object> response = new HashMap<>();
        // 将趋势数据放入响应Map
        response.put("data", trendData);
        // 设置响应状态码为200
        response.put("code", 200);
        // 设置响应消息为查询成功
        response.put("message", "查询成功");

        // 返回成功响应
        return ResponseEntity.ok(response);
    }
}