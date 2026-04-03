package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品分类Service接口
 * 提供商品分类相关的业务逻辑处理功能，包括商品分类信息的增删改查等操作
 *
 * @author 熊杨博
 * @date 2026-01-03
 */
public interface ProductCategoryService extends IService<ProductCategory>{


    List<ProductCategory> selectCategorybyshopid(Long shopid);
}
