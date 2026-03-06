package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Purchase;

import java.util.Map;

public interface PurchaseMapper extends BaseMapper<Purchase> {
    /**
     * 店铺管理员获取采购记录列表（包含产品名称和供应商名称）
     */
    Page<Purchase> getShopAdminPurchaseList(@Param("params") Map<String, Object> params);
}
