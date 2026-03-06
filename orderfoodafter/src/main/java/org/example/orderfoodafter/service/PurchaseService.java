package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.entity.Purchase;

import java.util.Map;

public interface PurchaseService extends IService<Purchase> {
    /**
     * 店铺管理员获取采购记录列表（包含产品名称和供应商名称）
     */
    PageInfo<Purchase> getShopAdminPurchaseList(Map<String, Object> params);
}
