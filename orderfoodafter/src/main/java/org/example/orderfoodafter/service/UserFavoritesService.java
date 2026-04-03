package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.entity.UserFavorites;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户收藏Service接口
 * 提供用户收藏相关的业务逻辑处理功能，包括用户收藏信息的增删改查等操作
 *
 * @author 赵康乐
 * @date 2025-11-23
 */
public interface UserFavoritesService extends IService<UserFavorites>{

    List<UserFavorites> selectFavoritesByUserId(Long userId);
}