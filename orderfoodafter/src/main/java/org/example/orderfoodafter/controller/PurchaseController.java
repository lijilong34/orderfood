// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis Plus的QueryWrapper类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入PageHelper的PageInfo类，用于分页信息封装
import com.github.pagehelper.PageInfo;
// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入Purchase实体类
import org.example.orderfoodafter.entity.Purchase;
// 导入PurchaseService服务接口
import org.example.orderfoodafter.service.PurchaseService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.*;

// 导入Java数学类BigDecimal，用于处理精确的金额计算
import java.math.BigDecimal;
// 导入Java日期类Date
import java.util.Date;
// 导入Java集合类List
import java.util.List;
// 导入Java集合类Map
import java.util.Map;

/**
 * 采购管理控制器
 * 负责管理店铺的采购业务，包括采购记录的创建、查询、更新、删除及统计分析等功能
 *
 * @author 陈逸磊
 * @date 2026-01-10
 */
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 设置该控制器的基础请求路径为/purchase
@RequestMapping("/purchase")
// 定义PurchaseController类，继承自BaseController基类
/**
 * PurchaseController类
 *
 * @author 陈逸磊
 * @date 2026-03-18
 */

public class PurchaseController {

    // 使用Autowired注解自动注入PurchaseService服务实例
    @Autowired
    // 声明PurchaseService服务对象
    private PurchaseService service;

    /**
     * 店铺管理员获取采购记录列表（分页，包含产品名称和供应商名称）
     */
    // 使用PostMapping注解映射POST请求到/shopadmin/list路径
    @PostMapping("/shopadmin/list")
    // 定义获取采购记录列表的方法，接收Map参数并返回R响应对象
        /**
     * 获取 getShopAdminPurchaseList
     * 
     * @return getShopAdminPurchaseList
     * @author 陈逸磊
     */
    public R getShopAdminPurchaseList(@RequestBody Map<String, Object> params) {
        // 使用try-catch块捕获异常
        try {
            // 调用service层的getShopAdminPurchaseList方法获取分页的采购记录列表
            PageInfo<Purchase> pageInfo = service.getShopAdminPurchaseList(params);
            // 返回成功的响应对象，包含分页信息
                /**
     * R
     * 
     * @author 陈逸磊
     */
            return new R().addData("pageInfo", pageInfo);
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常并包含错误信息
                /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
            throw new RuntimeException("获取采购记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员创建采购记录
     */
    // 使用PostMapping注解映射POST请求到/shopadmin/create路径
    @PostMapping("/shopadmin/create")
    // 定义创建采购记录的方法，接收Map参数并返回R响应对象
        /**
     * createPurchase
     * 
     * @author 陈逸磊
     */
    public R createPurchase(@RequestBody Map<String, Object> params) {
        // 使用try-catch块捕获异常
        try {
            // 创建新的Purchase对象实例
                /**
     * Purchase
     * 
     * @author 陈逸磊
     */
            Purchase purchase = new Purchase();

            // 验证必填字段：检查参数中是否包含shopId且不为空
            if (!params.containsKey("shopId") || params.get("shopId") == null) {
                // 如果shopId为空，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("店铺ID不能为空");
            }
            // 验证必填字段：检查参数中是否包含supplierId且不为空
            if (!params.containsKey("supplierId") || params.get("supplierId") == null) {
                // 如果supplierId为空，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("供应商ID不能为空");
            }
            // 验证必填字段：检查参数中是否包含productId且不为空
            if (!params.containsKey("productId") || params.get("productId") == null) {
                // 如果productId为空，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("产品ID不能为空");
            }
            // 验证必填字段：检查参数中是否包含quantity且不为空
            if (!params.containsKey("quantity") || params.get("quantity") == null) {
                // 如果quantity为空，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("采购数量不能为空");
            }
            // 验证必填字段：检查参数中是否包含unitPrice且不为空
            if (!params.containsKey("unitPrice") || params.get("unitPrice") == null) {
                // 如果unitPrice为空，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("采购单价不能为空");
            }

            // 从参数中获取shopId并转换为Long类型
            Long shopId = Long.parseLong(params.get("shopId").toString());
            // 从参数中获取supplierId并转换为Long类型
            Long supplierId = Long.parseLong(params.get("supplierId").toString());
            // 从参数中获取productId并转换为Long类型
            Long productId = Long.parseLong(params.get("productId").toString());
            // 从参数中获取quantity并转换为Integer类型
            Integer quantity = Integer.parseInt(params.get("quantity").toString());
            // 从参数中获取unitPrice并转换为BigDecimal类型
                /**
     * BigDecimal
     * 
     * @author 陈逸磊
     */
            BigDecimal unitPrice = new BigDecimal(params.get("unitPrice").toString());

            // 设置Purchase对象的shopId属性
            purchase.setShopId(shopId);
            // 设置Purchase对象的supplierId属性
            purchase.setSupplierId(supplierId);
            // 设置Purchase对象的productId属性
            purchase.setProductId(productId);
            // 设置Purchase对象的quantity属性
            purchase.setQuantity(quantity);
            // 设置Purchase对象的unitPrice属性
            purchase.setUnitPrice(unitPrice);

            // 计算总金额：单价乘以数量
                /**
     * multiply
     * 
     * @author 陈逸磊
     */
            BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity));
            // 设置Purchase对象的totalAmount属性
            purchase.setTotalAmount(totalAmount);

            // 设置采购日期：如果参数中包含purchaseDate且不为空
            if (params.containsKey("purchaseDate") && params.get("purchaseDate") != null) {
                // 获取purchaseDate参数的字符串值
                String purchaseDateStr = params.get("purchaseDate").toString();
                // 使用try-catch尝试解析日期
                try {
                    // 尝试将字符串解析为时间戳（长整型）
                    Long timestamp = Long.parseLong(purchaseDateStr);
                    // 将时间戳转换为Date对象并设置
                        /**
     * 设置 setPurchaseDate
     * 
     * @param setPurchaseDate setPurchaseDate
     * @author 陈逸磊
     */
                    purchase.setPurchaseDate(new Date(timestamp));
                } catch (NumberFormatException e) {
                    // 如果不是时间戳格式，尝试解析为日期字符串
                    try {
                        // 创建SimpleDateFormat对象，指定日期格式
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        // 解析日期字符串并设置
                        purchase.setPurchaseDate(sdf.parse(purchaseDateStr));
                    } catch (Exception ex) {
                        // 如果解析失败，使用当前时间作为默认值
                            /**
     * 设置 setPurchaseDate
     * 
     * @param setPurchaseDate setPurchaseDate
     * @author 陈逸磊
     */
                        purchase.setPurchaseDate(new Date());
                    }
                }
            } else {
                // 如果没有提供purchaseDate，使用当前时间
                    /**
     * 设置 setPurchaseDate
     * 
     * @param setPurchaseDate setPurchaseDate
     * @author 陈逸磊
     */
                purchase.setPurchaseDate(new Date());
            }

            // 设置采购状态：默认为1（已完成）
            purchase.setStatus((byte) 1);

            // 设置备注：如果参数中包含remark且不为空
            if (params.containsKey("remark") && params.get("remark") != null) {
                // 去除remark字符串两端的空格并设置
                purchase.setRemark(params.get("remark").toString().trim());
            }

            // 设置创建时间为当前时间
                /**
     * 设置 setCreateTime
     * 
     * @param setCreateTime setCreateTime
     * @author 陈逸磊
     */
            purchase.setCreateTime(new Date());
            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 陈逸磊
     */
            purchase.setUpdateTime(new Date());

            // 调用service层的save方法保存采购记录
            boolean result = service.save(purchase);

            // 判断保存是否成功
            if (result) {
                // 保存成功，返回成功响应，包含采购记录和成功消息
                    /**
     * R
     * 
     * @author 陈逸磊
     */
                return new R().addData("purchase", purchase).addData("message", "采购记录创建成功");
            } else {
                // 保存失败，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("采购记录创建失败");
            }
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常并包含错误信息
                /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
            throw new RuntimeException("采购记录创建失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员更新采购记录
     */
    // 使用PutMapping注解映射PUT请求到/shopadmin/update路径
    @PutMapping("/shopadmin/update")
    // 定义更新采购记录的方法，接收Map参数并返回R响应对象
        /**
     * updatePurchase
     * 
     * @author 陈逸磊
     */
    public R updatePurchase(@RequestBody Map<String, Object> params) {
        // 使用try-catch块捕获异常
        try {
            // 从参数中获取采购记录ID并转换为Long类型
            Long id = Long.parseLong(params.get("id").toString());

            // 根据ID查询采购记录
            Purchase purchase = service.getById(id);
            // 判断采购记录是否存在
            if (purchase == null) {
                // 如果采购记录不存在，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("采购记录不存在");
            }

            // 更新字段：如果参数中包含quantity
            if (params.containsKey("quantity")) {
                // 获取quantity参数并转换为Integer类型
                Integer quantity = Integer.parseInt(params.get("quantity").toString());
                // 设置Purchase对象的quantity属性
                purchase.setQuantity(quantity);

                // 重新计算总金额
                // 判断单价是否不为空
                if (purchase.getUnitPrice() != null) {
                    // 使用新的数量乘以单价计算总金额
                        /**
     * 获取 getUnitPrice
     * 
     * @return getUnitPrice
     * @author 陈逸磊
     */
                    BigDecimal totalAmount = purchase.getUnitPrice().multiply(new BigDecimal(quantity));
                    // 设置Purchase对象的totalAmount属性
                    purchase.setTotalAmount(totalAmount);
                }
            }
            // 更新字段：如果参数中包含unitPrice
            if (params.containsKey("unitPrice")) {
                // 获取unitPrice参数并转换为BigDecimal类型
                    /**
     * BigDecimal
     * 
     * @author 陈逸磊
     */
                BigDecimal unitPrice = new BigDecimal(params.get("unitPrice").toString());
                // 设置Purchase对象的unitPrice属性
                purchase.setUnitPrice(unitPrice);

                // 重新计算总金额
                // 判断数量是否不为空
                if (purchase.getQuantity() != null) {
                    // 使用新的单价乘以数量计算总金额
                        /**
     * multiply
     * 
     * @author 陈逸磊
     */
                    BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(purchase.getQuantity()));
                    // 设置Purchase对象的totalAmount属性
                    purchase.setTotalAmount(totalAmount);
                }
            }
            // 更新字段：如果参数中包含purchaseDate
            if (params.containsKey("purchaseDate")) {
                // 获取purchaseDate参数的字符串值
                String purchaseDateStr = params.get("purchaseDate").toString();
                // 使用try-catch尝试解析日期
                try {
                    // 尝试将字符串解析为时间戳（长整型）
                    Long timestamp = Long.parseLong(purchaseDateStr);
                    // 将时间戳转换为Date对象并设置
                        /**
     * 设置 setPurchaseDate
     * 
     * @param setPurchaseDate setPurchaseDate
     * @author 陈逸磊
     */
                    purchase.setPurchaseDate(new Date(timestamp));
                } catch (NumberFormatException e) {
                    // 如果不是时间戳格式，尝试解析为日期字符串
                    try {
                        // 创建SimpleDateFormat对象，指定日期格式
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        // 解析日期字符串并设置
                        purchase.setPurchaseDate(sdf.parse(purchaseDateStr));
                    } catch (Exception ex) {
                        // 如果解析失败，保持原日期不变
                    }
                }
            }
            // 更新字段：如果参数中包含status
            if (params.containsKey("status")) {
                // 获取status参数并转换为Byte类型
                purchase.setStatus(Byte.parseByte(params.get("status").toString()));
            }
            // 更新字段：如果参数中包含remark
            if (params.containsKey("remark")) {
                // 去除remark字符串两端的空格并设置
                purchase.setRemark(params.get("remark").toString().trim());
            }

            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 陈逸磊
     */
            purchase.setUpdateTime(new Date());

            // 调用service层的updateById方法更新采购记录
            boolean result = service.updateById(purchase);

            // 判断更新是否成功
            if (result) {
                // 更新成功，返回成功响应，包含采购记录和成功消息
                    /**
     * R
     * 
     * @author 陈逸磊
     */
                return new R().addData("purchase", purchase).addData("message", "采购记录更新成功");
            } else {
                // 更新失败，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("采购记录更新失败");
            }
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常并包含错误信息
                /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
            throw new RuntimeException("采购记录更新失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员删除采购记录
     */
    // 使用DeleteMapping注解映射DELETE请求到/shopadmin/delete/{id}路径
    @DeleteMapping("/shopadmin/delete/{id}")
    // 定义删除采购记录的方法，接收路径参数id并返回R响应对象
/**
 * deletePurchase方法
 *
 * @author 陈逸磊
 */
    public R deletePurchase(@PathVariable Long id) {
        // 使用try-catch块捕获异常
        try {
            // 根据ID查询采购记录
            Purchase purchase = service.getById(id);
            // 判断采购记录是否存在
            if (purchase == null) {
                // 如果采购记录不存在，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("采购记录不存在");
            }

            // 调用service层的removeById方法删除采购记录
            boolean result = service.removeById(id);

            // 判断删除是否成功
            if (result) {
                // 删除成功，返回成功响应，包含成功消息
                    /**
     * R
     * 
     * @author 陈逸磊
     */
                return new R().addData("message", "采购记录删除成功");
            } else {
                // 删除失败，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("采购记录删除失败");
            }
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常并包含错误信息
                /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
            throw new RuntimeException("采购记录删除失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取采购记录详情
     */
    // 使用GetMapping注解映射GET请求到/shopadmin/detail/{id}路径
    @GetMapping("/shopadmin/detail/{id}")
    // 定义获取采购记录详情的方法，接收路径参数id并返回R响应对象
/**
 * getPurchaseDetail方法
 *
 * @author 陈逸磊
 */
    public R getPurchaseDetail(@PathVariable Long id) {
        // 使用try-catch块捕获异常
        try {
            // 根据ID查询采购记录
            Purchase purchase = service.getById(id);
            // 判断采购记录是否存在
            if (purchase == null) {
                // 如果采购记录不存在，抛出运行时异常
                    /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
                throw new RuntimeException("采购记录不存在");
            }

            // 返回成功响应，包含采购记录详情
                /**
     * R
     * 
     * @author 陈逸磊
     */
            return new R().addData("purchase", purchase);
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常并包含错误信息
                /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
            throw new RuntimeException("获取采购记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取采购统计信息
     */
    // 使用GetMapping注解映射GET请求到/shopadmin/stats路径
    @GetMapping("/shopadmin/stats")
    // 定义获取采购统计信息的方法，接收请求参数shopId并返回R响应对象
        /**
     * 获取 getPurchaseStats
     * 
     * @return getPurchaseStats
     * @author 陈逸磊
     */
    public R getPurchaseStats(@RequestParam Long shopId) {
        // 使用try-catch块捕获异常
        try {
            // 创建QueryWrapper查询条件构建器
            QueryWrapper<Purchase> queryWrapper = new QueryWrapper<>();
            // 设置查询条件：shop_id等于传入的shopId
            queryWrapper.eq("shop_id", shopId);
            // 设置查询条件：status等于1（只统计已完成的采购）
            queryWrapper.eq("status", 1);

            // 查询符合条件的采购记录列表
            List<Purchase> purchaseList = service.list(queryWrapper);

            // 计算统计信息
            // 初始化采购记录总数
            int totalRecords = purchaseList.size();
            // 初始化总金额为0
            BigDecimal totalAmount = BigDecimal.ZERO;
            // 初始化总数量为0
            int totalQuantity = 0;

            // 遍历采购记录列表，计算总金额和总数量
            for (Purchase purchase : purchaseList) {
                // 判断采购记录的总金额是否不为空
                if (purchase.getTotalAmount() != null) {
                    // 累加采购记录的总金额
                    totalAmount = totalAmount.add(purchase.getTotalAmount());
                }
                // 判断采购记录的数量是否不为空
                if (purchase.getQuantity() != null) {
                    // 累加采购记录的数量
                    totalQuantity += purchase.getQuantity();
                }
            }

            // 创建Map对象存储统计信息
            Map<String, Object> stats = Map.of(
                // 存储采购记录总数
                "totalRecords", totalRecords,
                // 存储总金额
                "totalAmount", totalAmount,
                // 存储总数量
                "totalQuantity", totalQuantity
            );

            // 返回成功响应，包含统计信息
                /**
     * R
     * 
     * @author 陈逸磊
     */
            return new R().addData("stats", stats);
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常并包含错误信息
                /**
     * RuntimeException
     * 
     * @author 陈逸磊
     */
            throw new RuntimeException("获取采购统计信息失败: " + e.getMessage());
        }
    }
}