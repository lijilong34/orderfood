package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.entity.Evaluation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 评价Service接口
 * 提供评价相关的业务逻辑处理功能，包括评价信息的增删改查等操作
 *
 * @author 熊杨博
 * @date 2026-01-05
 */
public interface EvaluationService extends IService<Evaluation>{


    List<Evaluation> selectevaluationbyProductId(long productId);

    List<Evaluation> selectevaluationbyadmin(QueryWrapper queryWrapper);

    List<Evaluation> selectevaluationbyShopId(long shopId);

    /**
     * 根据查询条件获取店铺评价列表（支持连表查询）
     * @param queryWrapper 查询条件
     * @return 评价列表
     */
    List<Evaluation> selectevaluationbyShopIdWithQuery(com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Evaluation> queryWrapper);
}
