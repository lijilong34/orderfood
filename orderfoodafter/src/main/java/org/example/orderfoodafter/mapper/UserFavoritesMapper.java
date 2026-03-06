package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.UserFavorites;

import java.util.List;

public interface UserFavoritesMapper extends BaseMapper<UserFavorites> {
    List<UserFavorites> selectFavoritesByUserId(Long userId);
}