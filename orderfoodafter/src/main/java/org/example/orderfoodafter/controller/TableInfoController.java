// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis Plus的QueryWrapper类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入Product实体类
import org.example.orderfoodafter.entity.Product;
// 导入TableInfo实体类
import org.example.orderfoodafter.entity.TableInfo;
// 导入ProductService服务接口
import org.example.orderfoodafter.service.ProductService;
// 导入TableInfoService服务接口
import org.example.orderfoodafter.service.TableInfoService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.*;

// 导入Java集合类List
import java.util.List;

/**
 * 餐桌信息控制器
 * 负责管理餐厅餐桌信息，包括餐桌的查询、状态管理等功能
 *
 * @author 周子金
 * @date 2026-01-22
 */
// 使用RequestMapping注解设置该控制器的基础请求路径为/table
@RequestMapping("/table")
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 定义TableInfoController类，继承自BaseController基类，泛型为TableInfo和TableInfoService
/**
 * TableInfoController类
 *
 * @author 周子金
 * @date 2026-03-18
 */

public class TableInfoController extends BaseController<TableInfo, TableInfoService> {
    // 使用Autowired注解自动注入TableInfoService服务实例
    @Autowired
    // 声明TableInfoService服务对象
    private TableInfoService tableInfoService;

    // 使用Autowired注解自动注入ProductService服务实例
    @Autowired
    // 声明ProductService服务对象
    private ProductService productService;

    // 定义TableInfoController的构造函数，接收TableInfoService参数
/**
 * TableInfoController方法
 *
 * @author 周子金
 */
    public TableInfoController(TableInfoService tableInfoService) {
        // 调用父类BaseController的构造函数，传入tableInfoService
        super(tableInfoService);
        // 将传入的tableInfoService赋值给当前类的tableInfoService属性
        this.tableInfoService = tableInfoService;
    }

    // 使用GetMapping注解映射GET请求到/available路径
    @GetMapping("/available")
    // 定义获取可用餐桌的方法，接收shopId和productId请求参数，返回R响应对象
        /**
     * 获取 getAvailableTables
     * 
     * @return getAvailableTables
     * @author 周子金
     */
    public R getAvailableTables(@RequestParam(required = false) Long shopId,
                                @RequestParam(required = false) Long productId) {
        // 将shopId赋值给finalShopId变量
        Long finalShopId = shopId;

        // 判断productId是否不为空且大于0
        if (productId != null && productId > 0) {
            // 根据产品ID查询产品信息
            Product product = productService.getById(productId);
            // 判断产品是否存在
            if (product == null) {
                // 如果产品不存在，返回错误响应
                return R.error("产品不存在");
            }
            // 将产品的店铺ID赋值给finalShopId
            finalShopId = product.getShopId();
        }

        // 判断finalShopId是否为空或小于等于0
        if (finalShopId == null || finalShopId <= 0) {
            // 如果店铺ID为空，返回错误响应
            return R.error("店铺ID不能为空");
        }

        // 创建查询条件构建器
        QueryWrapper<TableInfo> queryWrapper = new QueryWrapper<>();
        // 设置查询条件：shop_id等于finalShopId
        queryWrapper.eq("shop_id", finalShopId);
        // 设置查询条件：status等于0（可用）
        queryWrapper.eq("status", 0);
        // 按桌号升序排列
        queryWrapper.orderByAsc("table_no");

        // 查询餐桌列表
        List<TableInfo> tableList = tableInfoService.list(queryWrapper);
        // 返回成功响应，包含餐桌列表
            /**
     * R
     * 
     * @author 周子金
     */
        return new R().addData("tableList", tableList);
    }
}