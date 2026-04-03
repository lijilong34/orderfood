// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入通用返回结果类R
import org.example.orderfoodafter.common.R;
// 导入商品实体类
import org.example.orderfoodafter.entity.Product;
// 导入商品服务类
import org.example.orderfoodafter.service.ProductService;
// 导入Spring框架的自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入GetMapping注解
import org.springframework.web.bind.annotation.GetMapping;
// 导入PathVariable注解
import org.springframework.web.bind.annotation.PathVariable;
// 导入RestController注解
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品购买控制器
 * 负责处理商品购买相关的操作，包括商品信息查询等功能
 * 
 * @author 李梦瑶
 * @date 2025-11-29
 */
// 使用RestController注解标识这是一个REST控制器
@RestController
// 定义ProductBuyConller控制器类
/**
 * ProductBuyConller类
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */

public class ProductBuyConller {
    // 使用Autowired注解自动注入ProductService服务
    @Autowired
    // 声明ProductService服务实例
    private ProductService productService;
    /**
     * 根据商品ID查询商品信息
     * 作者:李梦瑶
     * @param productid 商品ID
     * @return 商品信息
     */
    // 使用GetMapping注解映射GET请求到/selectproductinfobyproductid/{productid}路径
    @GetMapping("/selectproductinfobyproductid/{productid}")
    // 定义根据商品ID查询商品信息的方法，从路径变量获取商品ID，返回R类型对象
/**
 * selectProductInfoByProductId方法
 *
 * @author 李梦瑶
 */
    public R selectProductInfoByProductId(@PathVariable("productid") int productid) {
        // 调用服务层根据商品ID查询商品信息
        Product product=productService.selectproductinfobyproductid(productid);
        // 返回商品信息
            /**
     * R
     * 
     * @author 李吉隆
     */
        return new R().addData("product",product);
    }
}
