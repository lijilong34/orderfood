package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.Menus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 菜单Service接口
 * 提供菜单相关的业务逻辑处理功能，包括菜单信息的增删改查等操作
 * 
 * @author 李吉隆
 * @date 2025-12-17
 */
public interface MenusService extends IService<Menus>{


    List<Menus> selectMenusbyId(int userId);
}
