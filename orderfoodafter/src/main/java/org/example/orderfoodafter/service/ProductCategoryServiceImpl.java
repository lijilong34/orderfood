package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.ProductCategory;
import org.example.orderfoodafter.mapper.ProductCategoryMapper;
import org.example.orderfoodafter.service.ProductCategoryService;

/**
 * 商品分类Service实现类
 * 实现商品分类相关的业务逻辑处理功能
 *
 * @author 熊杨博
 * @date 2026-01-04
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService{
/**
 * selectCategorybyshopid方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<ProductCategory> selectCategorybyshopid(Long shopid) {
        List<ProductCategory> ProductCategoryList=baseMapper.selectCategorybyshopid(shopid);
      return ProductCategoryList;
    }
}
