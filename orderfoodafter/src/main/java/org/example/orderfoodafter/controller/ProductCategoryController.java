package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.ProductCategory;
import org.example.orderfoodafter.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ProductCategory")
public class ProductCategoryController extends BaseController<ProductCategory, ProductCategoryService>{
    @Autowired
    private ProductCategoryService productCategoryService;
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        super(productCategoryService);
        this.productCategoryService = productCategoryService;
    }
    @GetMapping("/selectcategorybyshopid/{shopid}")
    public R selectCategorybyshopid(@PathVariable("shopid") Long shopid){
        List<ProductCategory> productCategoryList=productCategoryService.selectCategorybyshopid(shopid);
        return new R().addData("productCategoryList",productCategoryList);
    }

}
