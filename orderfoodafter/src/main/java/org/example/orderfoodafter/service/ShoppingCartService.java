package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 购物车Service接口
 * 提供购物车相关的业务逻辑处理功能，包括购物车信息的增删改查等操作
 *
 * @author 赵康乐
 * @date 2025-11-22
 */
public interface ShoppingCartService extends IService<ShoppingCart>{


    List<ShoppingCart> selectshopcartbyuserid(int userid);
}
