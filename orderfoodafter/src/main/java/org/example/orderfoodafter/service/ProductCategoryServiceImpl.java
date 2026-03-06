package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.ProductCategory;
import org.example.orderfoodafter.mapper.ProductCategoryMapper;
import org.example.orderfoodafter.service.ProductCategoryService;
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService{

    @Override
    public List<ProductCategory> selectCategorybyshopid(Long shopid) {
        List<ProductCategory> ProductCategoryList=baseMapper.selectCategorybyshopid(shopid);
      return ProductCategoryList;
    }
}
