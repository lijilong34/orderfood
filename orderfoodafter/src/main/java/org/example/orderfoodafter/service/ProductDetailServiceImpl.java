package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.ProductDetail;
import org.example.orderfoodafter.mapper.ProductDetailMapper;
import org.example.orderfoodafter.service.ProductDetailService;

/**
 * 产品详情Service实现类
 * 实现产品详情相关的业务逻辑处理功能
 * 
 * @author 李吉隆
 * @date 2026-02-11
 */
@Service
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail> implements ProductDetailService{

}
