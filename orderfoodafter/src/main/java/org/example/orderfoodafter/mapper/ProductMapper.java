package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品Mapper接口
 * 用于对商品表进行数据访问操作，提供商品信息的增删改查功能
 *
 * @author 李梦瑶
 * @date 2026-03-18
 */
public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 执行自定义SQL查询
     * @param sql SQL语句
     * @param params 参数
     * @return 查询结果
     * @author 李梦瑶
     */
    List<Map<String, Object>> selectMapsBySql(@Param("sql") String sql, @Param("params") Object... params);
    
    /**
     * 查询销量前5的商品
     * @return 商品列表
     * @author 李梦瑶
     */
    List<Product> selectproducttop5();
    
    /**
     * 根据店铺查询所有商品
     * @param queryWrapper 查询条件
     * @return 商品列表
     * @author 李梦瑶
     */
    List<Product> selectallproductbyshop(@Param("ew") QueryWrapper<Product> queryWrapper);
    
    /**
     * 根据商品ID查询商品信息
     * @param productid 商品ID
     * @return 商品实体
     * @author 李梦瑶
     */
    Product selectproductinfobyproductid(@Param("productid") int productid);
    
    /**
     * 根据分类查询商品
     * @param queryWrapper 查询条件
     * @return 商品列表
     * @author 李梦瑶
     */
    List<Product> selectProductbycategory(@Param("ew") QueryWrapper queryWrapper);
    
    /**
     * 查询销量前10的商品
     * @return 商品列表
     * @author 李梦瑶
     */
    List<Product> selectproducttop10();
}
