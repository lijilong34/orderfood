package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.ShoppingCartMapper;
import org.example.orderfoodafter.entity.ShoppingCart;
import org.example.orderfoodafter.service.ShoppingCartService;

/**
 * 购物车Service实现类
 * 实现购物车相关的业务逻辑处理功能
 *
 * @author 赵康乐
 * @date 2025-11-23
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService{
/**
 * selectshopcartbyuserid方法
 *
 * @author 李吉隆
 */

    @Override
    public List<ShoppingCart> selectshopcartbyuserid(int userid) {
        List<ShoppingCart> shoppingCartList=baseMapper.selectshopcartbyuserid(userid);
        return shoppingCartList;
    }
}
