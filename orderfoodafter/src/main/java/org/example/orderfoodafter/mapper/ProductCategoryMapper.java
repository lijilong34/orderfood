package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类Mapper接口
 * 用于对商品分类表进行数据访问操作，提供商品分类信息的增删改查功能
 *
 * @author 熊杨博
 * @date 2026-01-03
 */
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {
    List<ProductCategory> selectCategorybyshopid(@Param("shopid") Long shopid);
}