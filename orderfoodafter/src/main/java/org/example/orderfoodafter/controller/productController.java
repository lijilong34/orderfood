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
// 导入商品实体类
import org.example.orderfoodafter.entity.Product;
// 导入商品服务类
import org.example.orderfoodafter.service.ProductService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Web的绑定注解
import org.springframework.web.bind.annotation.*;

// 导入List列表接口
import java.util.List;
// 导入Map映射接口
import java.util.Map;

/**
 * 菜品管理控制器
 * 提供菜品的增删改查功能，包括菜品查询、分类查询、热门菜品等
 * 
 * @author 李梦瑶
 * @date 2026-03-18
 */
// 使用RestController注解标识这是一个REST控制器
@RestController
// 设置请求映射路径为/product
@RequestMapping("/product")
// 定义ProductController控制器类，继承BaseController基础控制器

public class ProductController extends  BaseController<Product, ProductService> {
    // 使用Autowired注解自动注入ProductService服务
    @Autowired
    // 声明ProductService服务实例
    private ProductService productService;

    /**
     * 构造函数
     * @param productService 商品服务
     * @author 李梦瑶
     */
    public ProductController(ProductService productService) {
        // 调用父类构造函数，传入服务实例
        super(productService);
        // 将服务实例赋值给当前类的成员变量
        this.productService = productService;
    }

    /**
     * 根据菜品ID查询商品信息
     * @param id 商品ID
     * @return 商品信息
     * @author 李梦瑶
     */
    // 使用GetMapping注解映射GET请求到/getbyid/{id}路径
    @GetMapping("/getbyid/{id}")
    public R getById(@PathVariable("id") int id) {
        // 调用服务层根据商品ID查询商品信息
        Product product = productService.selectproductinfobyproductid(id);
        // 返回商品信息
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("entity", product);
    }


    /**
     * 新增菜品（对应前端"增加"按钮）
     * 前端请求：POST /product/add
     * 前端传递参数：{name: "菜品名", price: 19.9, ...}（表单数据）
     * @param product 商品对象
     * @return 新增结果
     * @author 李梦瑶
     */
    // 使用PostMapping注解映射POST请求到/addProduct路径
    @PostMapping("/addProduct")
    public R addProduct(@RequestBody Product product) {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层新增菜品
            boolean success = productService.addProduct(product);
            // 检查新增结果
            if (success) {
                // 新增成功，返回成功消息
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "新增菜品成功");
            }
            // 新增失败，抛出异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("新增菜品失败");
        } catch (Exception e) {
            // 捕获异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("新增菜品失败：" + e.getMessage());
        }
    }

    /**
     * 编辑菜品（对应前端"编辑"按钮）
     * 前端请求：POST /product/updateProduct
     * 前端传递参数：{id: 1, name: "菜品名", price: 19.9, ...}（表单数据）
     * @param product 商品对象
     * @return 编辑结果
     * @author 李梦瑶
     */
    // 使用PostMapping注解映射POST请求到/updateProduct路径
    @PostMapping("/updateProduct")
    public R updateProduct(@RequestBody Product product) {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层编辑菜品
            boolean success = productService.updateProduct(product);
            // 检查编辑结果
            if (success) {
                // 编辑成功，返回成功消息
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "编辑菜品成功");
            }
            // 编辑失败，抛出异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("编辑菜品失败");
        } catch (Exception e) {
            // 捕获异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("编辑菜品失败：" + e.getMessage());
        }
    }


    /**
     * 删除菜品（对应前端"删除"按钮）
     * 前端请求：GET /product/delete/{id}
     * @param id 商品ID
     * @return 删除结果
     * @author 李梦瑶
     */
    // 使用GetMapping注解映射GET请求到/delete/{id}路径
    @GetMapping("/delete/{id}")
    public R deleteProduct(@PathVariable("id") Long id) {
        // 使用try-catch块处理可能的异常
        try {
            // 调用服务层删除菜品
            boolean success = productService.deleteProduct(id);
            // 检查删除结果
            if (success) {
                // 删除成功，返回成功消息
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "删除菜品成功");
            }
            // 删除失败，抛出异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("删除菜品失败");
        } catch (Exception e) {
            // 捕获异常，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("删除菜品失败：" + e.getMessage());
        }
    }
    /**
     * 查询所有菜品
     * @param selectwhere 查询条件
     * @return 查询结果
     * @throws Exception 异常信息
     * @author 李梦瑶
     */
    // 使用PostMapping注解映射POST请求到/selectallproduct路径
    @PostMapping("/selectallproduct")
    public R selectallproduct(@RequestBody Map<String, Object> selectwhere) throws Exception{
        // 从查询条件中获取where列表 map集合转换
        List where = (List) selectwhere.get("where");

        // 构建查询条件包装器 获取集合 封装条件
        QueryWrapper queryWrapper = commontUtil.getWhere(where);
        // 检查是否包含分页参数 不为空转换为整数
        if (selectwhere.get("page") != null) {
            // 获取页码并转换为整数  判断为空不空
            int page = Integer.parseInt(selectwhere.get("page").toString());

            // 启动分页，每页8条记录
            PageHelper.startPage(page, 8);
        }
        // 调用服务层查询所有菜品 查询
        List<Product> productList=productService.list(queryWrapper);
        // 创建分页信息对象
        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        // 返回分页数据
            /**
     * R
     * 
     * @author 李梦瑶
     */
           // 分页和查询传回前端
        return new R().addData("pageInfo", pageInfo);
    }

    /**
     * 根据分类查询菜品
     * @param selectwhere 查询条件
     * @return 查询结果
     * @throws Exception 异常信息
     * @author 李梦瑶
     */
    // 使用PostMapping注解映射POST请求到/selectproductbycategory路径
    @PostMapping("/selectproductbycategory")
    public R selectProductbycategory(@RequestBody Map<String,Object> selectwhere) throws Exception{
        // 从查询条件中获取where列表
        List where = (List) selectwhere.get("where");

        // 构建查询条件包装器
        QueryWrapper queryWrapper = commontUtil.getWhere(where);

        // 获取页码并转换为整数
        int page = Integer.parseInt(selectwhere.get("page").toString());

        // 启动分页，每页20条记录
        PageHelper.startPage(page, 20);

        // 调用服务层根据分类查询菜品
        List<Product> Productlist=productService.selectProductbycategory(queryWrapper);

        // 创建分页信息对象
        PageInfo pageInfo = new PageInfo<>(Productlist);

        // 返回分页数据
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("pageInfo", pageInfo);

    }
    /**
     * 查询Top30菜品
     * @return 菜品列表
     * @author 李梦瑶
     */
    // 使用GetMapping注解映射GET请求到/selectproducttop30路径
    @GetMapping("/selectproducttop30")
    public R selectproducttop30(){
        // 调用服务层查询Top10菜品
        List<Product> productList=productService.selectproducttop10();
        // 返回菜品列表
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("productList", productList);
    }

    /**
     * 查询商店所有菜品
     * @param shopId 商店ID
     * @return 菜品列表
     * @author 李梦瑶
     */
    // 使用GetMapping注解映射GET请求到/selectallproductbyshop路径
    @GetMapping("/selectallproductbyshop")
    public R selectallproductbyshop(@RequestParam("shopId") Long shopId){
        // 调用服务层查询商店所有菜品
        List<Product> productList=productService.selectallproductbyshop(shopId);
        // 返回菜品列表
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("productList", productList);
    }
}