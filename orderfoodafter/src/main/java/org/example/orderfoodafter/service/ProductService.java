package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductService extends IService<Product>{


    List<Product> selectproducttop5();

    /**
     * 根据ID查询菜品详情（编辑/查看功能使用）
     * @param id 菜品ID
     * @return 菜品实体
     */
    Product getProductById(Long id);

    /**
     * 新增菜品（适配前端增加表单）
     * @param product 菜品实体（前端表单提交数据）
     * @return 新增是否成功
     */
    boolean addProduct(Product product);

    /**
     * 编辑菜品（适配前端编辑表单）
     * @param product 菜品实体（包含ID和修改后的字段）
     * @return 编辑是否成功
     */
    boolean updateProduct(Product product);

    /**
     * 删除菜品（适配前端删除按钮）
     * @param id 菜品ID
     * @return 删除是否成功
     */
    boolean deleteProduct(Long id);

    List<Product> selectallproductbyshop(QueryWrapper<Product> queryWrapper);
    
    /**
     * 根据店铺ID查询商品列表
     * @param shopId 店铺ID
     * @return 商品列表
     */
    List<Product> selectallproductbyshop(Long shopId);

    Product selectproductinfobyproductid(int productid);

    List<Product> selectProductbycategory(QueryWrapper queryWrapper);

    List<Product> selectproducttop10();
}
