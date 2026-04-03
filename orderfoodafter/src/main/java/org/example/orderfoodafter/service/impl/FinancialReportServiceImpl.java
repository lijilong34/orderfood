package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.FinancialReport;
import org.example.orderfoodafter.mapper.FinancialReportMapper;
import org.example.orderfoodafter.service.FinancialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 财务报表Service实现类
 * 实现财务报表相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2026-01-19
 */
@Service
public class FinancialReportServiceImpl extends ServiceImpl<FinancialReportMapper, FinancialReport> implements FinancialReportService {

    @Autowired
    private FinancialReportMapper financialReportMapper;
/**
 * generateReport方法
 *
 * @author 熊杨博
 */

    @Override
    public FinancialReport generateReport(Long shopId, Date reportDate, Byte reportType) {
        // 查询店铺名称
        String shopName = getShopName(shopId);

        // 计算日期范围
        Date startDate = getStartDate(reportDate, reportType);
        Date endDate = getEndDate(reportDate, reportType);

        // 获取统计数据
        Map<String, Object> stats = financialReportMapper.getShopFinancialStats(shopId, startDate, endDate);
        Integer totalCustomers = financialReportMapper.getShopCustomers(shopId, startDate, endDate);

        // 创建报表对象
            /**
     * FinancialReport
     * 
     * @author 李吉隆
     */
        FinancialReport report = new FinancialReport();
        report.setShopId(shopId);
        report.setShopName(shopName);
        report.setReportDate(reportDate);
        report.setReportType(reportType);

        // 设置统计数据
        report.setTotalOrders(((Number) stats.getOrDefault("totalOrders", 0)).intValue());
        report.setCompletedOrders(((Number) stats.getOrDefault("completedOrders", 0)).intValue());
        report.setCancelledOrders(((Number) stats.getOrDefault("cancelledOrders", 0)).intValue());
        report.setTotalAmount((BigDecimal) stats.getOrDefault("totalAmount", BigDecimal.ZERO));
        report.setPayAmount((BigDecimal) stats.getOrDefault("payAmount", BigDecimal.ZERO));
        report.setDiscountAmount((BigDecimal) stats.getOrDefault("discountAmount", BigDecimal.ZERO));
        report.setWechatPayAmount((BigDecimal) stats.getOrDefault("wechatPayAmount", BigDecimal.ZERO));
        report.setAlipayAmount((BigDecimal) stats.getOrDefault("alipayAmount", BigDecimal.ZERO));
        report.setBalancePayAmount((BigDecimal) stats.getOrDefault("balancePayAmount", BigDecimal.ZERO));
        report.setAvgOrderAmount((BigDecimal) stats.getOrDefault("avgOrderAmount", BigDecimal.ZERO));
        report.setTotalCustomers(totalCustomers != null ? totalCustomers : 0);
        report.setNewCustomers(0); // 暂时设为0,后续可根据实际业务逻辑计算

        // 计算完成率
        if (report.getTotalOrders() > 0) {
                /**
     * BigDecimal
     * 
     * @author 李吉隆
     */
            BigDecimal completionRate = new BigDecimal(report.getCompletedOrders())
                        /**
     * divide
     * 
     * @author 李吉隆
     */
                    .divide(new BigDecimal(report.getTotalOrders()), 4, RoundingMode.HALF_UP)
                        /**
     * multiply
     * 
     * @author 李吉隆
     */
                    .multiply(new BigDecimal(100));
            report.setAvgOrderAmount(completionRate);
        }

        return report;
    }
/**
 * getFinancialStats方法
 *
 * @author 熊杨博
 */

    @Override
    public Map<String, Object> getFinancialStats(Long shopId, Date startDate, Date endDate) {
        Map<String, Object> stats = financialReportMapper.getShopFinancialStats(shopId, startDate, endDate);
        Integer totalCustomers = financialReportMapper.getShopCustomers(shopId, startDate, endDate);
        stats.put("totalCustomers", totalCustomers);
        return stats;
    }
/**
 * getFinancialReportList方法
 *
 * @author 熊杨博
 */

    @Override
    public List<FinancialReport> getFinancialReportList(Long shopId, Byte reportType, Date startDate, Date endDate) {
        return financialReportMapper.getFinancialReportList(shopId, reportType, startDate, endDate);
    }
/**
 * getOverviewData方法
 *
 * @author 熊杨博
 */

    @Override
    public Map<String, Object> getOverviewData(Long shopId) {
        // 获取今日数据
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date todayStart = today.getTime();

        today.add(Calendar.DAY_OF_MONTH, 1);
        Date todayEnd = today.getTime();

        Map<String, Object> todayStats = financialReportMapper.getShopFinancialStats(shopId, todayStart, todayEnd);

        // 获取本月数据
        Calendar monthStart = Calendar.getInstance();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        monthStart.set(Calendar.HOUR_OF_DAY, 0);
        monthStart.set(Calendar.MINUTE, 0);
        monthStart.set(Calendar.SECOND, 0);
        monthStart.set(Calendar.MILLISECOND, 0);

        Calendar monthEnd = Calendar.getInstance();
        monthEnd.add(Calendar.MONTH, 1);
        monthEnd.set(Calendar.DAY_OF_MONTH, 1);
        monthEnd.set(Calendar.HOUR_OF_DAY, 0);
        monthEnd.set(Calendar.MINUTE, 0);
        monthEnd.set(Calendar.SECOND, 0);
        monthEnd.set(Calendar.MILLISECOND, 0);

        Map<String, Object> monthStats = financialReportMapper.getShopFinancialStats(shopId, monthStart.getTime(), monthEnd.getTime());

        // 获取总订单数
        Map<String, Object> totalStats = financialReportMapper.getShopFinancialStats(shopId, null, null);

        Map<String, Object> result = new HashMap<>();
        result.put("today", todayStats);
        result.put("month", monthStats);
        result.put("total", totalStats);

        return result;
    }
/**
 * getShopName方法
 *
 * @author 熊杨博
 */

    private String getShopName(Long shopId) {
        // TODO: 从shop表获取店铺名称
        return "店铺" + shopId;
    }
/**
 * getStartDate方法
 *
 * @author 熊杨博
 */

    private Date getStartDate(Date reportDate, Byte reportType) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(reportDate);

        switch (reportType) {
            case 1: // 日报
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            case 2: // 月报
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            case 3: // 季报
                int month = cal.get(Calendar.MONTH);
                int quarter = month / 3;
                cal.set(Calendar.MONTH, quarter * 3);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            case 4: // 年报
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
        }

        return cal.getTime();
    }
/**
 * getEndDate方法
 *
 * @author 熊杨博
 */

    private Date getEndDate(Date reportDate, Byte reportType) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(reportDate);

        switch (reportType) {
            case 1: // 日报
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                break;
            case 2: // 月报
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                break;
            case 3: // 季报
                int month = cal.get(Calendar.MONTH);
                int quarter = month / 3;
                cal.set(Calendar.MONTH, (quarter + 1) * 3 - 1);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                break;
            case 4: // 年报
                cal.set(Calendar.MONTH, 11);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                break;
        }

        return cal.getTime();
    }
/**
 * generateDailyReports方法
 *
 * @author 熊杨博
 */

    @Override
    public List<FinancialReport> generateDailyReports(Long shopId, Date startDate, Date endDate) {
        List<FinancialReport> reports = new ArrayList<>();
        
        // 获取日期范围内的所有日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        
        while (!cal.getTime().after(endDate)) {
            Date currentDate = cal.getTime();
            
            // 计算当天的起始和结束时间
            Calendar dayStart = Calendar.getInstance();
            dayStart.setTime(currentDate);
            dayStart.set(Calendar.HOUR_OF_DAY, 0);
            dayStart.set(Calendar.MINUTE, 0);
            dayStart.set(Calendar.SECOND, 0);
            dayStart.set(Calendar.MILLISECOND, 0);
            
            Calendar dayEnd = Calendar.getInstance();
            dayEnd.setTime(currentDate);
            dayEnd.set(Calendar.HOUR_OF_DAY, 23);
            dayEnd.set(Calendar.MINUTE, 59);
            dayEnd.set(Calendar.SECOND, 59);
            dayEnd.set(Calendar.MILLISECOND, 999);
            
            // 获取当天的统计数据
            Map<String, Object> stats = financialReportMapper.getShopFinancialStats(shopId, dayStart.getTime(), dayEnd.getTime());
            Integer totalCustomers = financialReportMapper.getShopCustomers(shopId, dayStart.getTime(), dayEnd.getTime());
            
            // 创建报表对象
                /**
     * FinancialReport
     * 
     * @author 李吉隆
     */
            FinancialReport report = new FinancialReport();
            report.setShopId(shopId);
            report.setShopName(getShopName(shopId));
            report.setReportDate(currentDate);
            report.setReportType((byte) 1); // 日报
            
            // 设置统计数据
            report.setTotalOrders(((Number) stats.getOrDefault("totalOrders", 0)).intValue());
            report.setCompletedOrders(((Number) stats.getOrDefault("completedOrders", 0)).intValue());
            report.setCancelledOrders(((Number) stats.getOrDefault("cancelledOrders", 0)).intValue());
            report.setTotalAmount((BigDecimal) stats.getOrDefault("totalAmount", BigDecimal.ZERO));
            report.setPayAmount((BigDecimal) stats.getOrDefault("payAmount", BigDecimal.ZERO));
            report.setDiscountAmount((BigDecimal) stats.getOrDefault("discountAmount", BigDecimal.ZERO));
            report.setWechatPayAmount((BigDecimal) stats.getOrDefault("wechatPayAmount", BigDecimal.ZERO));
            report.setAlipayAmount((BigDecimal) stats.getOrDefault("alipayAmount", BigDecimal.ZERO));
            report.setBalancePayAmount((BigDecimal) stats.getOrDefault("balancePayAmount", BigDecimal.ZERO));
            report.setAvgOrderAmount((BigDecimal) stats.getOrDefault("avgOrderAmount", BigDecimal.ZERO));
            report.setTotalCustomers(totalCustomers != null ? totalCustomers : 0);
            report.setNewCustomers(0);
            
            // 只添加有订单数据的日期
            if (report.getTotalOrders() > 0) {
                reports.add(report);
            }
            
            // 移动到下一天
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return reports;
    }
}