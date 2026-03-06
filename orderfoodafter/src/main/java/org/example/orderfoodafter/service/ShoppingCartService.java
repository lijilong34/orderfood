package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart>{


    List<ShoppingCart> selectshopcartbyuserid(int userid);
}
