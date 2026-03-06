package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 执行自定义SQL查询
     */
    List<Map<String, Object>> selectMapsBySql(@Param("sql") String sql, @Param("params") Object... params);
    
    /**
     * 查询销量前5的商品
     */
    List<Product> selectproducttop5();
    
    /**
     * 根据店铺查询所有商品
     */
    List<Product> selectallproductbyshop(@Param("ew") QueryWrapper<Product> queryWrapper);
    
    /**
     * 根据商品ID查询商品信息
     */
    Product selectproductinfobyproductid(@Param("productid") int productid);
    
    /**
     * 根据分类查询商品
     */
    List<Product> selectProductbycategory(@Param("ew") QueryWrapper queryWrapper);
    
    /**
     * 查询销量前10的商品
     */
    List<Product> selectproducttop10();
}
