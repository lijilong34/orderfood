package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Shop;

import java.util.List;
import java.util.Map;

public interface ShopMapper extends BaseMapper<Shop> {
    List<Shop> selectShopBytop();

     List<Shop> selectShopBylist(@Param("id")int id,@Param("cid") int cid,@Param("nickname") String nickname ,@Param("phone")String phone);

    List<Shop> selectcategoryforshop(@Param("name") String name);
    
    List<Long> selectShopIdsByCategory(@Param("name") String name);
    
    List<Shop> selectShopWithCategoriesByIds(@Param("shopIds") List<Long> shopIds);
    
    /**
     * 执行自定义SQL查询
     */
    List<Map<String, Object>> selectMapsBySql(@Param("sql") String sql, @Param("params") Object... params);
    
    /**
     * 查询店铺订单趋势数据
     */
    List<Map<String, Object>> selectShopOrderTrend(@Param("shopId") Long shopId);
    
    /**
     * 查询店铺销售额趋势数据
     */
    List<Map<String, Object>> selectShopSalesTrend(@Param("shopId") Long shopId);
    
    /**
     * 查询热门商品数据
     */
    List<Map<String, Object>> selectHotProducts(@Param("shopId") Long shopId);

    List<Shop> selectallProductbyconut(@Param("ew") QueryWrapper<Shop> queryWrapper);

    List<Shop> selectShopsWithProductsByIds(@Param("shopIds") List<Long> shopIds);
}
