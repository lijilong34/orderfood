package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Shop;

import java.util.List;
import java.util.Map;

/**
 * 店铺Mapper接口
 * 用于对店铺表进行数据访问操作，提供店铺信息的增删改查功能
 *
 * @author 周子金
 * @date 2026-03-18
 */
public interface ShopMapper extends BaseMapper<Shop> {
    /**
     * 查询热门店铺
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectShopBytop();

    /**
     * 多条件查询店铺
     * @param id 店铺ID
     * @param cid 分类ID
     * @param nickname 昵称
     * @param phone 电话
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectShopBylist(@Param("id")int id,@Param("cid") int cid,@Param("nickname") String nickname ,@Param("phone")String phone);

    /**
     * 根据分类名称查询店铺
     * @param name 分类名称
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectcategoryforshop(@Param("name") String name);
    
    /**
     * 根据分类名称查询店铺ID列表
     * @param name 分类名称
     * @return 店铺ID列表
     * @author 周子金
     */
    List<Long> selectShopIdsByCategory(@Param("name") String name);
    
    /**
     * 根据店铺ID列表查询店铺及其分类
     * @param shopIds 店铺ID列表
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectShopWithCategoriesByIds(@Param("shopIds") List<Long> shopIds);
    
    /**
     * 执行自定义SQL查询
     * @param sql SQL语句
     * @param params 参数
     * @return 查询结果
     * @author 周子金
     */
    List<Map<String, Object>> selectMapsBySql(@Param("sql") String sql, @Param("params") Object... params);
    
    /**
     * 查询店铺订单趋势数据
     * @param shopId 店铺ID
     * @return 订单趋势数据
     * @author 周子金
     */
    List<Map<String, Object>> selectShopOrderTrend(@Param("shopId") Long shopId);
    
    /**
     * 查询店铺销售额趋势数据
     * @param shopId 店铺ID
     * @return 销售额趋势数据
     * @author 周子金
     */
    List<Map<String, Object>> selectShopSalesTrend(@Param("shopId") Long shopId);
    
    /**
     * 查询热门商品数据
     * @param shopId 店铺ID
     * @return 热门商品数据
     * @author 周子金
     */
    List<Map<String, Object>> selectHotProducts(@Param("shopId") Long shopId);

    /**
     * 查询店铺及其商品数量
     * @param queryWrapper 查询条件
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectallProductbyconut(@Param("ew") QueryWrapper<Shop> queryWrapper);

    /**
     * 根据店铺ID列表查询店铺及其商品
     * @param shopIds 店铺ID列表
     * @return 店铺列表
     * @author 周子金
     */
    List<Shop> selectShopsWithProductsByIds(@Param("shopIds") List<Long> shopIds);
}
