package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.UserFavorites;
import org.example.orderfoodafter.mapper.UserFavoritesMapper;
import org.example.orderfoodafter.service.UserFavoritesService;
@Service
public class UserFavoritesServiceImpl extends ServiceImpl<UserFavoritesMapper, UserFavorites> implements UserFavoritesService{

    @Override
    public List<UserFavorites> selectFavoritesByUserId(Long userId) {
        List<UserFavorites> userFavoritesList = baseMapper.selectFavoritesByUserId(userId);
        return userFavoritesList;
    }
}