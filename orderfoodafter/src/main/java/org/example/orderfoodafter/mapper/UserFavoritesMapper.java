package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.UserFavorites;

import java.util.List;

/**
 * 用户收藏Mapper接口
 * 用于对用户收藏表进行数据访问操作，提供用户收藏信息的增删改查功能
 *
 * @author 赵康乐
 * @date 2025-11-23
 */
public interface UserFavoritesMapper extends BaseMapper<UserFavorites> {
    List<UserFavorites> selectFavoritesByUserId(Long userId);
}