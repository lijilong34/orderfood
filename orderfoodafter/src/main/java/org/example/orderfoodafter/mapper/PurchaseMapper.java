package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Purchase;

import java.util.Map;

/**
 * 采购Mapper接口
 * 用于对采购表进行数据访问操作，提供采购信息的增删改查功能
 *
 * @author 陈逸磊
 * @date 2026-01-10
 */
public interface PurchaseMapper extends BaseMapper<Purchase> {
    /**
     * 店铺管理员获取采购记录列表（包含产品名称和供应商名称）
     */
    Page<Purchase> getShopAdminPurchaseList(@Param("params") Map<String, Object> params);
}
