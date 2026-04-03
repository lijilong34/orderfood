package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.UserFavorites;
import org.example.orderfoodafter.mapper.UserFavoritesMapper;
import org.example.orderfoodafter.service.UserFavoritesService;

/**
 * 用户收藏Service实现类
 * 实现用户收藏相关的业务逻辑处理功能
 *
 * @author 赵康乐
 * @date 2025-11-24
 */
@Service
public class UserFavoritesServiceImpl extends ServiceImpl<UserFavoritesMapper, UserFavorites> implements UserFavoritesService{
/**
 * selectFavoritesByUserId方法
 *
 * @author 李吉隆
 */

    @Override
    public List<UserFavorites> selectFavoritesByUserId(Long userId) {
        List<UserFavorites> userFavoritesList = baseMapper.selectFavoritesByUserId(userId);
        return userFavoritesList;
    }
}