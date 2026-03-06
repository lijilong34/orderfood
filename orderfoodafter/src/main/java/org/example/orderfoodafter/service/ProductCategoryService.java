package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductCategoryService extends IService<ProductCategory>{


    List<ProductCategory> selectCategorybyshopid(Long shopid);
}
