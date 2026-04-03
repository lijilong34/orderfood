package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.Evaluation;
import org.example.orderfoodafter.mapper.EvaluationMapper;

/**
 * 评价Service实现类
 * 实现评价相关的业务逻辑处理功能
 *
 * @author 熊杨博
 * @date 2026-01-06
 */
@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService{
/**
 * selectevaluationbyProductId方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Evaluation> selectevaluationbyProductId(long productId) {
        List<Evaluation> evaluationList=baseMapper.selectevaluationbyProductId(productId);
        return evaluationList;
    }
/**
 * selectevaluationbyadmin方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Evaluation> selectevaluationbyadmin(QueryWrapper queryWrapper) {
        List<Evaluation> evaluationList=baseMapper.selectevaluationbyadmin(queryWrapper);
        return evaluationList;
    }
/**
 * selectevaluationbyShopId方法
 *
 * @author 李梦瑶
 */
    @Override
    public List<Evaluation> selectevaluationbyShopId(long shopId) {
        List<Evaluation> evaluationList=baseMapper.selectevaluationbyShopId(shopId);
        return evaluationList;
    }
/**
 * selectevaluationbyShopIdWithQuery方法
 *
 * @author 李梦瑶
 */

    @Override
    public List<Evaluation> selectevaluationbyShopIdWithQuery(com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Evaluation> queryWrapper) {
        List<Evaluation> evaluationList = baseMapper.selectevaluationbyShopIdWithQuery(queryWrapper);
        return evaluationList;
    }
}
