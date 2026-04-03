package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.orderfoodafter.entity.Evaluation;
import java.util.List;
import java.util.Map;

/**
 * 评价Mapper接口
 * 用于对评价表进行数据访问操作，提供评价信息的增删改查功能
 *
 * @author 熊杨博
 * @date 2026-01-05
 */
public interface EvaluationMapper extends BaseMapper<Evaluation> {

    List<Evaluation> selectevaluationbyProductId(@Param("ProductId") long productId);

    List<Evaluation> selectevaluationbyadmin(@Param("ew") com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Evaluation> queryWrapper);
    
    List<Evaluation> selectevaluationbyShopId(@Param("ShopId") long shopId);

    /**
     * 根据查询条件获取店铺评价列表（支持连表查询）
     * @param queryWrapper 查询条件
     * @return 评价列表
     */
    List<Evaluation> selectevaluationbyShopIdWithQuery(@Param("ew") com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Evaluation> queryWrapper);
}