package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 店铺Service接口
 * 提供店铺相关的业务逻辑处理功能，包括店铺信息的增删改查等操作
 *
 * @author 周子金
 * @date 2026-03-18
 */
public interface ShopService extends IService<Shop>{
    /**
     * 根据分类名称查询店铺
     * @param categoryname 分类名称
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectcategoryforshop(String categoryname) ;
    
    /**
     * 根据分类名称查询店铺ID列表
     * @param categoryname 分类名称
     * @return 店铺ID列表
     * @author 周子金
     */
    List<Long> selectShopIdsByCategory(String categoryname);
    
    /**
     * 根据店铺ID列表查询店铺及其分类
     * @param shopIds 店铺ID列表
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectShopWithCategoriesByIds(List<Long> shopIds);

    /**
     * 查询热门店铺
     * @return 店铺列表
     * @author 周子金
     */
    List selectShopBytop();

    /**
     * 多条件查询店铺
     * @param id 店铺ID
     * @param cid 分类ID
     * @param nickname 昵称
     * @param phone 电话
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectShopBylist(@Param("id") int id,@Param("cid") int cid,@Param("nickname") String nickname,@Param("phone") String phone);
    
    /**
     * 获取店铺仪表板数据
     * @param shopId 店铺ID
     * @return 仪表板数据
     * @author 周子金
     */
    Map<String, Object> getShopDashboardData(Long shopId);
    
    /**
     * 获取热门商品数据
     * @param shopId 店铺ID
     * @return 热门商品数据
     * @author 周子金
     */
    List<Map<String, Object>> getHotProducts(Long shopId);
    
    /**
     * 获取最新订单数据
     * @param shopId 店铺ID
     * @return 最新订单数据
     * @author 周子金
     */
    List<Map<String, Object>> getLatestOrders(Long shopId);
    
    /**
     * 获取店铺趋势数据
     * @param shopId 店铺ID
     * @return 店铺趋势数据
     * @author 周子金
     */
    Map<String, Object> getShopTrendData(Long shopId);

    /**
     * 查询店铺及其商品
     * @param queryWrapper 查询条件
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectallandProduct(QueryWrapper<Shop> queryWrapper);

    /**
     * 根据店铺ID列表查询店铺及其商品
     * @param shopIds 店铺ID列表
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectShopsWithProductsByIds(List<Long> shopIds);
}
