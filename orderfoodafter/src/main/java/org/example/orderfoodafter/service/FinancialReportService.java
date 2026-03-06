package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.orderfoodafter.entity.FinancialReport;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 财务报表Service
 */
public interface FinancialReportService extends IService<FinancialReport> {

    /**
     * 生成财务报表
     */
    FinancialReport generateReport(Long shopId, Date reportDate, Byte reportType);

    /**
     * 获取财务统计数据
     */
    Map<String, Object> getFinancialStats(Long shopId, Date startDate, Date endDate);

    /**
     * 获取财务报表列表
     */
    List<FinancialReport> getFinancialReportList(Long shopId, Byte reportType, Date startDate, Date endDate);

    /**
     * 获取总览数据
     */
    Map<String, Object> getOverviewData(Long shopId);

    /**
     * 生成每日报表列表(实时计算)
     */
    List<FinancialReport> generateDailyReports(Long shopId, Date startDate, Date endDate);
}