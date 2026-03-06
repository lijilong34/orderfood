package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.ShoppingCartMapper;
import org.example.orderfoodafter.entity.ShoppingCart;
import org.example.orderfoodafter.service.ShoppingCartService;
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService{

    @Override
    public List<ShoppingCart> selectshopcartbyuserid(int userid) {
        List<ShoppingCart> shoppingCartList=baseMapper.selectshopcartbyuserid(userid);
        return shoppingCartList;
    }
}
