package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.ProductCategory;

import java.util.List;

public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {
    List<ProductCategory> selectCategorybyshopid(@Param("shopid") Long shopid);
}