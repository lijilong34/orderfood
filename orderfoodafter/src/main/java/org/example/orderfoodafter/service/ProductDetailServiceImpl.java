package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.ProductDetail;
import org.example.orderfoodafter.mapper.ProductDetailMapper;
import org.example.orderfoodafter.service.ProductDetailService;
@Service
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail> implements ProductDetailService{

}
