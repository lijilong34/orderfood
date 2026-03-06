package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Product;
import org.example.orderfoodafter.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductBuyConller {
    @Autowired
    private ProductService productService;
    @GetMapping("/selectproductinfobyproductid/{productid}")
    public R selectProductInfoByProductId(@PathVariable("productid") int productid) {
        Product product=productService.selectproductinfobyproductid(productid);
        return new R().addData("product",product);
    }
}
