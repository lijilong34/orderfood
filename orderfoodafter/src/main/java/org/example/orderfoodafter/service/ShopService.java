package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ShopService extends IService<Shop>{
     List<Shop> selectcategoryforshop(String categoryname) ;
     
     List<Long> selectShopIdsByCategory(String categoryname);
     
     List<Shop> selectShopWithCategoriesByIds(List<Long> shopIds);

    List selectShopBytop();


    List<Shop> selectShopBylist(@Param("id") int id,@Param("cid") int cid,@Param("nickname") String nickname,@Param("phone") String phone);
    
    /**
     * 获取店铺仪表板数据
     */
    Map<String, Object> getShopDashboardData(Long shopId);
    
    /**
     * 获取热门商品数据
     */
    List<Map<String, Object>> getHotProducts(Long shopId);
    
    /**
     * 获取最新订单数据
     */
    List<Map<String, Object>> getLatestOrders(Long shopId);
    
    /**
     * 获取店铺趋势数据
     */
    Map<String, Object> getShopTrendData(Long shopId);

    List<Shop> selectallandProduct(QueryWrapper<Shop> queryWrapper);

    List<Shop> selectShopsWithProductsByIds(List<Long> shopIds);
}
