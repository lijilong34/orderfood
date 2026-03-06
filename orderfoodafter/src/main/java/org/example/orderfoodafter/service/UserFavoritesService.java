package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.entity.UserFavorites;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserFavoritesService extends IService<UserFavorites>{

    List<UserFavorites> selectFavoritesByUserId(Long userId);
}