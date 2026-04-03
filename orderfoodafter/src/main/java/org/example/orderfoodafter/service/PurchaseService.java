package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.entity.Purchase;

import java.util.Map;

/**
 * 采购Service接口
 * 提供采购相关的业务逻辑处理功能，包括采购信息的增删改查等操作
 *
 * @author 陈逸磊
 * @date 2026-01-10
 */
public interface PurchaseService extends IService<Purchase> {
    /**
     * 店铺管理员获取采购记录列表（包含产品名称和供应商名称）
     */
    PageInfo<Purchase> getShopAdminPurchaseList(Map<String, Object> params);
}
