package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Purchase;
import org.example.orderfoodafter.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 采购管理控制器
 */
@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService service;

    /**
     * 店铺管理员获取采购记录列表（分页，包含产品名称和供应商名称）
     */
    @PostMapping("/shopadmin/list")
    public R getShopAdminPurchaseList(@RequestBody Map<String, Object> params) {
        try {
            PageInfo<Purchase> pageInfo = service.getShopAdminPurchaseList(params);
            return new R().addData("pageInfo", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取采购记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员创建采购记录
     */
    @PostMapping("/shopadmin/create")
    public R createPurchase(@RequestBody Map<String, Object> params) {
        try {
            Purchase purchase = new Purchase();

            // 验证必填字段
            if (!params.containsKey("shopId") || params.get("shopId") == null) {
                return R.error("店铺ID不能为空");
            }
            if (!params.containsKey("supplierId") || params.get("supplierId") == null) {
                return R.error("供应商ID不能为空");
            }
            if (!params.containsKey("productId") || params.get("productId") == null) {
                return R.error("产品ID不能为空");
            }
            if (!params.containsKey("quantity") || params.get("quantity") == null) {
                return R.error("采购数量不能为空");
            }
            if (!params.containsKey("unitPrice") || params.get("unitPrice") == null) {
                return R.error("采购单价不能为空");
            }

            Long shopId = Long.parseLong(params.get("shopId").toString());
            Long supplierId = Long.parseLong(params.get("supplierId").toString());
            Long productId = Long.parseLong(params.get("productId").toString());
            Integer quantity = Integer.parseInt(params.get("quantity").toString());
            BigDecimal unitPrice = new BigDecimal(params.get("unitPrice").toString());

            purchase.setShopId(shopId);
            purchase.setSupplierId(supplierId);
            purchase.setProductId(productId);
            purchase.setQuantity(quantity);
            purchase.setUnitPrice(unitPrice);
            
            // 计算总金额
            BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity));
            purchase.setTotalAmount(totalAmount);

            // 设置采购日期，默认为当前时间
            if (params.containsKey("purchaseDate") && params.get("purchaseDate") != null) {
                String purchaseDateStr = params.get("purchaseDate").toString();
                try {
                    // 尝试解析时间戳
                    Long timestamp = Long.parseLong(purchaseDateStr);
                    purchase.setPurchaseDate(new Date(timestamp));
                } catch (NumberFormatException e) {
                    // 如果不是时间戳，尝试解析日期字符串
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        purchase.setPurchaseDate(sdf.parse(purchaseDateStr));
                    } catch (Exception ex) {
                        purchase.setPurchaseDate(new Date());
                    }
                }
            } else {
                purchase.setPurchaseDate(new Date());
            }

            // 默认状态为1（已完成）
            purchase.setStatus((byte) 1);

            if (params.containsKey("remark") && params.get("remark") != null) {
                purchase.setRemark(params.get("remark").toString().trim());
            }

            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());

            boolean result = service.save(purchase);

            if (result) {
                return new R().addData("purchase", purchase).addData("message", "采购记录创建成功");
            } else {
                return R.error("采购记录创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("采购记录创建失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员更新采购记录
     */
    @PutMapping("/shopadmin/update")
    public R updatePurchase(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.parseLong(params.get("id").toString());

            Purchase purchase = service.getById(id);
            if (purchase == null) {
                return R.error("采购记录不存在");
            }

            // 更新字段
            if (params.containsKey("quantity")) {
                Integer quantity = Integer.parseInt(params.get("quantity").toString());
                purchase.setQuantity(quantity);
                
                // 重新计算总金额
                if (purchase.getUnitPrice() != null) {
                    BigDecimal totalAmount = purchase.getUnitPrice().multiply(new BigDecimal(quantity));
                    purchase.setTotalAmount(totalAmount);
                }
            }
            if (params.containsKey("unitPrice")) {
                BigDecimal unitPrice = new BigDecimal(params.get("unitPrice").toString());
                purchase.setUnitPrice(unitPrice);
                
                // 重新计算总金额
                if (purchase.getQuantity() != null) {
                    BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(purchase.getQuantity()));
                    purchase.setTotalAmount(totalAmount);
                }
            }
            if (params.containsKey("purchaseDate")) {
                String purchaseDateStr = params.get("purchaseDate").toString();
                try {
                    // 尝试解析时间戳
                    Long timestamp = Long.parseLong(purchaseDateStr);
                    purchase.setPurchaseDate(new Date(timestamp));
                } catch (NumberFormatException e) {
                    // 如果不是时间戳，尝试解析日期字符串
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        purchase.setPurchaseDate(sdf.parse(purchaseDateStr));
                    } catch (Exception ex) {
                        // 如果解析失败，保持原日期不变
                    }
                }
            }
            if (params.containsKey("status")) {
                purchase.setStatus(Byte.parseByte(params.get("status").toString()));
            }
            if (params.containsKey("remark")) {
                purchase.setRemark(params.get("remark").toString().trim());
            }

            purchase.setUpdateTime(new Date());

            boolean result = service.updateById(purchase);

            if (result) {
                return new R().addData("purchase", purchase).addData("message", "采购记录更新成功");
            } else {
                return R.error("采购记录更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("采购记录更新失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员删除采购记录
     */
    @DeleteMapping("/shopadmin/delete/{id}")
    public R deletePurchase(@PathVariable Long id) {
        try {
            Purchase purchase = service.getById(id);
            if (purchase == null) {
                return R.error("采购记录不存在");
            }

            boolean result = service.removeById(id);

            if (result) {
                return new R().addData("message", "采购记录删除成功");
            } else {
                return R.error("采购记录删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("采购记录删除失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取采购记录详情
     */
    @GetMapping("/shopadmin/detail/{id}")
    public R getPurchaseDetail(@PathVariable Long id) {
        try {
            Purchase purchase = service.getById(id);
            if (purchase == null) {
                return R.error("采购记录不存在");
            }

            return new R().addData("purchase", purchase);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取采购记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取采购统计信息
     */
    @GetMapping("/shopadmin/stats")
    public R getPurchaseStats(@RequestParam Long shopId) {
        try {
            QueryWrapper<Purchase> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("shop_id", shopId);
            queryWrapper.eq("status", 1); // 只统计已完成的采购

            List<Purchase> purchaseList = service.list(queryWrapper);

            // 计算统计信息
            int totalRecords = purchaseList.size();
            BigDecimal totalAmount = BigDecimal.ZERO;
            int totalQuantity = 0;

            for (Purchase purchase : purchaseList) {
                if (purchase.getTotalAmount() != null) {
                    totalAmount = totalAmount.add(purchase.getTotalAmount());
                }
                if (purchase.getQuantity() != null) {
                    totalQuantity += purchase.getQuantity();
                }
            }

            Map<String, Object> stats = Map.of(
                "totalRecords", totalRecords,
                "totalAmount", totalAmount,
                "totalQuantity", totalQuantity
            );

            return new R().addData("stats", stats);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取采购统计信息失败: " + e.getMessage());
        }
    }
}
