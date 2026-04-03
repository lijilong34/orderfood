package org.example.orderfoodafter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.entity.Purchase;
import org.example.orderfoodafter.mapper.PurchaseMapper;
import org.example.orderfoodafter.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 采购Service实现类
 * 实现采购相关的业务逻辑处理功能
 * 
 * @author 陈逸磊
 * @date 2026-01-14
 */
@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements PurchaseService {

    @Autowired
    private PurchaseMapper purchaseMapper;
/**
 * getShopAdminPurchaseList方法
 *
 * @author 陈逸磊
 */

    @Override
    public PageInfo<Purchase> getShopAdminPurchaseList(Map<String, Object> params) {
        // 分页参数
        int page = 1;
        int pageSize = 10;

        if (params.containsKey("page") && params.get("page") != null) {
            try {
                page = Integer.parseInt(params.get("page").toString());
            } catch (NumberFormatException e) {
                System.err.println("page转换失败: " + params.get("page"));
            }
        }

        if (params.containsKey("pageSize") && params.get("pageSize") != null) {
            try {
                pageSize = Integer.parseInt(params.get("pageSize").toString());
            } catch (NumberFormatException e) {
                System.err.println("pageSize转换失败: " + params.get("pageSize"));
            }
        }

        // 过滤掉空字符串参数
        if (params.containsKey("productName")) {
            Object productName = params.get("productName");
            if (productName == null || productName.toString().trim().isEmpty()) {
                params.remove("productName");
            }
        }

        if (params.containsKey("supplierName")) {
            Object supplierName = params.get("supplierName");
            if (supplierName == null || supplierName.toString().trim().isEmpty()) {
                params.remove("supplierName");
            }
        }

        if (params.containsKey("status")) {
            Object status = params.get("status");
            if (status == null || status.toString().trim().isEmpty()) {
                params.remove("status");
            }
        }

        // 打印详细参数
        System.out.println("采购记录查询参数:");
        System.out.println("  shopId: " + params.get("shopId") + " (类型: " + (params.get("shopId") != null ? params.get("shopId").getClass().getSimpleName() : "null") + ")");
        System.out.println("  productName: " + params.get("productName") + " (类型: " + (params.get("productName") != null ? params.get("productName").getClass().getSimpleName() : "null") + ")");
        System.out.println("  supplierName: " + params.get("supplierName") + " (类型: " + (params.get("supplierName") != null ? params.get("supplierName").getClass().getSimpleName() : "null") + ")");
        System.out.println("  status: " + params.get("status") + " (类型: " + (params.get("status") != null ? params.get("status").getClass().getSimpleName() : "null") + ")");
        System.out.println("  page: " + page);
        System.out.println("  pageSize: " + pageSize);

        // 使用PageHelper分页
        PageHelper.startPage(page, pageSize);

        // 调用Mapper方法，使用XML中的JOIN查询
        List<Purchase> purchaseList = purchaseMapper.getShopAdminPurchaseList(params);

        System.out.println("查询结果数量: " + purchaseList.size());

        return new PageInfo<>(purchaseList);
    }
}
