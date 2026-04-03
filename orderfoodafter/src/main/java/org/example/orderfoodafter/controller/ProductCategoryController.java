// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis Plus的查询条件包装器
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入PageHelper分页助手
import com.github.pagehelper.PageHelper;
// 导入PageInfo分页信息类
import com.github.pagehelper.PageInfo;
// 导入通用返回结果类R
import org.example.orderfoodafter.common.R;
// 导入商品分类实体类
import org.example.orderfoodafter.entity.ProductCategory;
// 导入商品分类服务类
import org.example.orderfoodafter.service.ProductCategoryService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Web的绑定注解
import org.springframework.web.bind.annotation.*;

// 导入List列表接口
import java.util.List;
// 导入Map映射接口
import java.util.Map;

/**
 * 商品分类控制器
 * 负责管理商品分类信息，包括分类的查询、创建、更新、删除等功能
 * 
 * @author 李梦瑶
 * @date 2026-01-03
 */
// 使用RestController注解标识这是一个REST控制器
@RestController
// 设置请求映射路径为/ProductCategory
@RequestMapping("/ProductCategory")
// 定义ProductCategoryController控制器类，继承BaseController基础控制器
/**
 * ProductCategoryController类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class ProductCategoryController extends BaseController<ProductCategory, ProductCategoryService>{
    // 使用Autowired注解自动注入ProductCategoryService服务
    @Autowired
    // 声明ProductCategoryService服务实例
    private ProductCategoryService productCategoryService;
    // 定义构造函数，接收ProductCategoryService服务参数
/**
 * ProductCategoryController方法
 *
 * @author 李梦瑶
 */
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        // 调用父类构造函数，传入服务实例
        super(productCategoryService);
        // 将服务实例赋值给当前类的成员变量
        this.productCategoryService = productCategoryService;
    }
    /**
     * 根据店铺ID查询商品分类
     * 作者:熊杨博
     * @param shopid 店铺ID
     * @return 商品分类列表
     */
    // 使用GetMapping注解映射GET请求到/selectcategorybyshopid/{shopid}路径
    @GetMapping("/selectcategorybyshopid/{shopid}")
    // 定义根据店铺ID查询商品分类的方法，从路径变量获取店铺ID，返回R类型对象
/**
 * selectCategorybyshopid方法
 *
 * @author 李梦瑶
 */
    public R selectCategorybyshopid(@PathVariable("shopid") Long shopid){
        // 调用服务层根据店铺ID查询商品分类
        List<ProductCategory> productCategoryList=productCategoryService.selectCategorybyshopid(shopid);
        // 返回商品分类列表
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("productCategoryList",productCategoryList);
    }

}
