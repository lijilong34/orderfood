package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.FinancialReport;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 财务报表Mapper
 */
@Mapper
public interface FinancialReportMapper extends BaseMapper<FinancialReport> {

    /**
     * 获取店铺财务统计数据
     * 当shopId为null时获取所有店铺的汇总数据
     */
    Map<String, Object> getShopFinancialStats(@Param("shopId") Long shopId,
                                               @Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate);

    /**
     * 获取店铺客户数
     * 当shopId为null时获取所有店铺的客户数
     */
    Integer getShopCustomers(@Param("shopId") Long shopId,
                              @Param("startDate") Date startDate,
                              @Param("endDate") Date endDate);

    /**
     * 获取时间段内的财务报表列表
     */
    List<FinancialReport> getFinancialReportList(@Param("shopId") Long shopId,
                                                   @Param("reportType") Byte reportType,
                                                   @Param("startDate") Date startDate,
                                                   @Param("endDate") Date endDate);
}