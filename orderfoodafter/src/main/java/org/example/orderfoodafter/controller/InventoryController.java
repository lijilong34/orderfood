// 定义库存管理控制器的包路径
package org.example.orderfoodafter.controller;
// 导入MyBatis Plus的查询条件包装器类
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入PageHelper分页插件
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类
import com.github.pagehelper.PageInfo;
// 导入通用响应结果类
import org.example.orderfoodafter.common.R;
// 导入库存实体类
import org.example.orderfoodafter.entity.Inventory;
// 导入库存服务接口
import org.example.orderfoodafter.service.InventoryService;
// 导入Spring的自动装配注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的请求映射注解集合
import org.springframework.web.bind.annotation.*;
// 导入Java的BigDecimal类
import java.math.BigDecimal;
// 导入Java的Date类
import java.util.Date;
// 导入Java的List集合类
import java.util.List;
// 导入Java的Map接口
import java.util.Map;

/**
 * 库存管理控制器
 * 负责管理店铺的库存信息，包括库存的创建、查询、更新、删除、调整及统计分析等功能
 * 
 * @author 熊杨博
 * @date 2026-01-12
 */
// 标记该类为REST控制器，所有方法返回JSON格式数据
@RestController
// 定义该控制器的请求路径前缀为/inventory
@RequestMapping("/inventory")
// 定义库存管理控制器类，继承基础控制器，指定实体类型为Inventory，服务类型为InventoryService
/**
 * InventoryController类
 *
 * @author 熊杨博
 * @date 2026-03-18
 */

public class InventoryController extends BaseController<Inventory, InventoryService> {

    // 构造函数，接收库存服务作为参数
/**
 * InventoryController方法
 *
 * @author 熊杨博
 */
    @Autowired
    public InventoryController(InventoryService service) {
        // 调用父类构造函数，传入服务实例
        super(service);
    }

    /**
     * 店铺管理员获取库存列表（分页）
     */
    // 定义处理获取库存列表的POST请求接口，路径为/shopadmin/list
    @PostMapping("/shopadmin/list")
    // 定义获取库存列表的方法，接收查询参数Map对象
        /**
     * 获取 getShopAdminInventoryList
     * 
     * @return getShopAdminInventoryList
     * @author 熊杨博
     */
    public R getShopAdminInventoryList(@RequestBody Map<String, Object> params) {
        // 使用try-catch捕获可能的异常
        try {
            // 获取页码参数，如果不存在则默认为1
            int page = params.containsKey("page") ? Integer.parseInt(params.get("page").toString()) : 1;
            // 获取每页大小参数，如果不存在则默认为10
            int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            // 获取店铺ID参数，如果不存在则为null
            Long shopId = params.containsKey("shopId") ? Long.parseLong(params.get("shopId").toString()) : null;

            // 启动分页
            PageHelper.startPage(page, pageSize);

            // 使用连表查询获取供应商名称
            List<Inventory> inventoryList = service.selectInventoryWithSupplier(params);
            // 将库存列表封装为分页信息对象
            PageInfo<Inventory> pageInfo = new PageInfo<>(inventoryList);

            // 返回分页信息数据
                /**
     * R
     * 
     * @author 熊杨博
     */
            return new R().addData("pageInfo", pageInfo);
        // 捕获异常
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应
            return R.error("获取库存列表失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员创建库存记录
     */
    // 定义处理创建库存记录的POST请求接口，路径为/shopadmin/create
    @PostMapping("/shopadmin/create")
    // 定义创建库存记录的方法，接收库存参数Map对象
        /**
     * createInventory
     * 
     * @author 熊杨博
     */
    public R createInventory(@RequestBody Map<String, Object> params) {
        // 使用try-catch捕获可能的异常
        try {
            // 创建新的库存实体对象
                /**
     * Inventory
     * 
     * @author 熊杨博
     */
            Inventory inventory = new Inventory();

            // 验证必填字段
            // 检查食材名称是否存在且不为空
            if (!params.containsKey("materialName") || params.get("materialName") == null || params.get("materialName").toString().trim().isEmpty()) {
                // 如果食材名称为空，返回错误响应
                return R.error("食材名称不能为空");
            }
            // 检查店铺ID是否存在且不为空
            if (!params.containsKey("shopId") || params.get("shopId") == null) {
                // 如果店铺ID为空，返回错误响应
                return R.error("店铺ID不能为空");
            }

            // 设置食材名称
            inventory.setMaterialName(params.get("materialName").toString().trim());
            
            // 如果参数中包含规格
            if (params.containsKey("spec") && params.get("spec") != null) {
                // 设置规格
                inventory.setSpec(params.get("spec").toString().trim());
            }
            
            // 如果参数中包含单位
            if (params.containsKey("unit") && params.get("unit") != null) {
                // 设置单位
                inventory.setUnit(params.get("unit").toString().trim());
            }
            
            // 如果参数中包含库存数量
            if (params.containsKey("stockQuantity") && params.get("stockQuantity") != null) {
                // 设置库存数量
                    /**
     * 设置 setStockQuantity
     * 
     * @param setStockQuantity setStockQuantity
     * @author 熊杨博
     */
                inventory.setStockQuantity(new BigDecimal(params.get("stockQuantity").toString()));
            } else {
                // 否则设置库存数量为0
                inventory.setStockQuantity(BigDecimal.ZERO);
            }
            
            // 如果参数中包含警告阈值
            if (params.containsKey("warningThreshold") && params.get("warningThreshold") != null) {
                // 设置警告阈值
                    /**
     * 设置 setWarningThreshold
     * 
     * @param setWarningThreshold setWarningThreshold
     * @author 熊杨博
     */
                inventory.setWarningThreshold(new BigDecimal(params.get("warningThreshold").toString()));
            } else {
                // 否则设置警告阈值为5
                    /**
     * 设置 setWarningThreshold
     * 
     * @param setWarningThreshold setWarningThreshold
     * @author 熊杨博
     */
                inventory.setWarningThreshold(new BigDecimal("5"));
            }

            // 如果参数中包含供应商ID
            if (params.containsKey("supplierId") && params.get("supplierId") != null) {
                // 设置供应商ID
                inventory.setSupplierId(Long.parseLong(params.get("supplierId").toString()));
            }
            
            // 获取店铺ID
            Long shopId = Long.parseLong(params.get("shopId").toString());
            // 设置店铺ID
            inventory.setShopId(shopId);
            
            // 设置创建时间为当前时间
                /**
     * 设置 setCreateTime
     * 
     * @param setCreateTime setCreateTime
     * @author 熊杨博
     */
            inventory.setCreateTime(new Date());
            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 熊杨博
     */
            inventory.setUpdateTime(new Date());

            // 调用服务保存库存记录
            boolean result = service.save(inventory);

            // 判断保存是否成功
            if (result) {
                // 如果保存成功，返回成功响应，包含库存记录和成功消息
                    /**
     * R
     * 
     * @author 熊杨博
     */
                return new R().addData("inventory", inventory).addData("message", "库存记录创建成功");
            } else {
                // 如果保存失败，返回错误响应
                return R.error("库存记录创建失败");
            }
        // 捕获异常
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应
            return R.error("库存记录创建失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员更新库存记录
     */
    // 定义处理更新库存记录的PUT请求接口，路径为/shopadmin/update
    @PutMapping("/shopadmin/update")
    // 定义更新库存记录的方法，接收库存参数Map对象
        /**
     * updateInventory
     * 
     * @author 熊杨博
     */
    public R updateInventory(@RequestBody Map<String, Object> params) {
        // 使用try-catch捕获可能的异常
        try {
            // 获取库存ID
            Long id = Long.parseLong(params.get("id").toString());

            // 根据ID查询库存记录
            Inventory inventory = service.getById(id);
            // 判断库存记录是否存在
            if (inventory == null) {
                // 如果不存在，返回错误响应
                return R.error("库存记录不存在");
            }

            // 更新字段
            // 如果参数中包含食材名称
            if (params.containsKey("materialName")) {
                // 更新食材名称
                inventory.setMaterialName(params.get("materialName").toString().trim());
            }
            // 如果参数中包含规格
            if (params.containsKey("spec")) {
                // 更新规格
                inventory.setSpec(params.get("spec").toString().trim());
            }
            // 如果参数中包含单位
            if (params.containsKey("unit")) {
                // 更新单位
                inventory.setUnit(params.get("unit").toString().trim());
            }
            // 如果参数中包含库存数量
            if (params.containsKey("stockQuantity")) {
                // 更新库存数量
                    /**
     * 设置 setStockQuantity
     * 
     * @param setStockQuantity setStockQuantity
     * @author 熊杨博
     */
                inventory.setStockQuantity(new BigDecimal(params.get("stockQuantity").toString()));
            }
            // 如果参数中包含警告阈值
            if (params.containsKey("warningThreshold")) {
                // 更新警告阈值
                    /**
     * 设置 setWarningThreshold
     * 
     * @param setWarningThreshold setWarningThreshold
     * @author 熊杨博
     */
                inventory.setWarningThreshold(new BigDecimal(params.get("warningThreshold").toString()));
            }
            // 如果参数中包含供应商ID
            if (params.containsKey("supplierId")) {
                // 更新供应商ID
                inventory.setSupplierId(Long.parseLong(params.get("supplierId").toString()));
            }

            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 熊杨博
     */
            inventory.setUpdateTime(new Date());

            // 调用服务更新库存记录
            boolean result = service.updateById(inventory);

            // 判断更新是否成功
            if (result) {
                // 如果更新成功，返回成功响应，包含库存记录和成功消息
                    /**
     * R
     * 
     * @author 熊杨博
     */
                return new R().addData("inventory", inventory).addData("message", "库存记录更新成功");
            } else {
                // 如果更新失败，返回错误响应
                return R.error("库存记录更新失败");
            }
        // 捕获异常
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应
            return R.error("库存记录更新失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员删除库存记录
     */
    // 定义处理删除库存记录的DELETE请求接口，路径为/shopadmin/delete/{id}
    @DeleteMapping("/shopadmin/delete/{id}")
    // 定义删除库存记录的方法，接收库存ID路径参数
/**
 * deleteInventory方法
 *
 * @author 熊杨博
 */
    public R deleteInventory(@PathVariable Long id) {
        // 使用try-catch捕获可能的异常
        try {
            // 根据ID查询库存记录
            Inventory inventory = service.getById(id);
            // 判断库存记录是否存在
            if (inventory == null) {
                // 如果不存在，返回错误响应
                return R.error("库存记录不存在");
            }

            // 根据ID删除库存记录
            boolean result = service.removeById(id);

            // 判断删除是否成功
            if (result) {
                // 如果删除成功，返回成功响应，包含成功消息
                    /**
     * R
     * 
     * @author 熊杨博
     */
                return new R().addData("message", "库存记录删除成功");
            } else {
                // 如果删除失败，返回错误响应
                return R.error("库存记录删除失败");
            }
        // 捕获异常
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应
            return R.error("库存记录删除失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取库存详情
     */
    // 定义处理获取库存详情的GET请求接口，路径为/shopadmin/detail/{id}
    @GetMapping("/shopadmin/detail/{id}")
    // 定义获取库存详情的方法，接收库存ID路径参数
/**
 * getInventoryDetail方法
 *
 * @author 熊杨博
 */
    public R getInventoryDetail(@PathVariable Long id) {
        // 使用try-catch捕获可能的异常
        try {
            // 根据ID查询库存记录
            Inventory inventory = service.getById(id);
            // 判断库存记录是否存在
            if (inventory == null) {
                // 如果不存在，返回错误响应
                return R.error("库存记录不存在");
            }

            // 返回库存记录数据
                /**
     * R
     * 
     * @author 熊杨博
     */
            return new R().addData("inventory", inventory);
        // 捕获异常
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应
            return R.error("获取库存详情失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员调整库存
     */
    // 定义处理调整库存的POST请求接口，路径为/shopadmin/adjust
    @PostMapping("/shopadmin/adjust")
    // 定义调整库存的方法，接收调整参数Map对象
        /**
     * adjustStock
     * 
     * @author 熊杨博
     */
    public R adjustStock(@RequestBody Map<String, Object> params) {
        // 使用try-catch捕获可能的异常
        try {
            // 获取库存ID
            Long id = Long.parseLong(params.get("id").toString());
            // 获取调整数量
                /**
     * BigDecimal
     * 
     * @author 熊杨博
     */
            BigDecimal adjustment = new BigDecimal(params.get("adjustment").toString());

            // 根据ID查询库存记录
            Inventory inventory = service.getById(id);
            // 判断库存记录是否存在
            if (inventory == null) {
                // 如果不存在，返回错误响应
                return R.error("库存记录不存在");
            }

            // 计算新的库存数量
            BigDecimal newStock = inventory.getStockQuantity().add(adjustment);
            // 判断新的库存数量是否为负数
            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                // 如果为负数，返回错误响应
                return R.error("库存不能为负数");
            }

            // 设置新的库存数量
            inventory.setStockQuantity(newStock);
            // 设置更新时间为当前时间
                /**
     * 设置 setUpdateTime
     * 
     * @param setUpdateTime setUpdateTime
     * @author 熊杨博
     */
            inventory.setUpdateTime(new Date());

            // 调用服务更新库存记录
            boolean result = service.updateById(inventory);

            // 判断更新是否成功
            if (result) {
                // 如果更新成功，返回成功响应，包含库存记录和成功消息
                    /**
     * R
     * 
     * @author 熊杨博
     */
                return new R().addData("inventory", inventory).addData("message", "库存调整成功");
            } else {
                // 如果更新失败，返回错误响应
                return R.error("库存调整失败");
            }
        // 捕获异常
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应
            return R.error("库存调整失败: " + e.getMessage());
        }
    }

    /**
     * 店铺管理员获取库存统计
     */
    // 定义处理获取库存统计的GET请求接口，路径为/shopadmin/stats
    @GetMapping("/shopadmin/stats")
    // 定义获取库存统计的方法，接收店铺ID参数
        /**
     * 获取 getInventoryStats
     * 
     * @return getInventoryStats
     * @author 熊杨博
     */
    public R getInventoryStats(@RequestParam Long shopId) {
        // 使用try-catch捕获可能的异常
        try {
            // 创建查询条件包装器对象
            QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
            // 设置查询条件：店铺ID等于传入的店铺ID
            queryWrapper.eq("shop_id", shopId);

            // 根据查询条件查询所有库存记录
            List<Inventory> allInventory = service.list(queryWrapper);

            // 初始化统计变量
            int totalCount = allInventory.size();
            int outOfStockCount = 0;
            int lowStockCount = 0;
            int sufficientStockCount = 0;

            // 遍历所有库存记录
            for (Inventory inv : allInventory) {
                // 获取库存数量
                BigDecimal stock = inv.getStockQuantity();
                // 判断库存数量是否为0
                if (stock.compareTo(BigDecimal.ZERO) == 0) {
                    // 如果为0，缺货计数加1
                    outOfStockCount++;
                // 判断库存数量是否小于等于5
                } else if (stock.compareTo(new BigDecimal("5")) <= 0) {
                    // 如果小于等于5，低库存计数加1
                    lowStockCount++;
                } else {
                    // 否则，库存充足计数加1
                    sufficientStockCount++;
                }
            }

            // 创建统计结果Map对象
            Map<String, Object> stats = Map.of(
                "totalCount", totalCount,
                "outOfStockCount", outOfStockCount,
                "lowStockCount", lowStockCount,
                "sufficientStockCount", sufficientStockCount
            );

            // 返回统计数据
                /**
     * R
     * 
     * @author 熊杨博
     */
            return new R().addData("stats", stats);
        // 捕获异常
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应
            return R.error("获取库存统计失败: " + e.getMessage());
        }
    }
}