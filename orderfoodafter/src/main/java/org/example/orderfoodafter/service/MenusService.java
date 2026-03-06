package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.Menus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MenusService extends IService<Menus>{


    List<Menus> selectMenusbyId(int userId);
}
