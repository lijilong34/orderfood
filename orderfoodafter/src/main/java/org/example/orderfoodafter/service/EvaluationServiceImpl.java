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

@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService{

    @Override
    public List<Evaluation> selectevaluationbyProductId(long productId) {
        List<Evaluation> evaluationList=baseMapper.selectevaluationbyProductId(productId);
        return evaluationList;
    }

    @Override
    public List<Evaluation> selectevaluationbyadmin(QueryWrapper queryWrapper) {
        List<Evaluation> evaluationList=baseMapper.selectevaluationbyadmin(queryWrapper);
        return evaluationList;
    }
    @Override
    public List<Evaluation> selectevaluationbyShopId(long shopId) {
        List<Evaluation> evaluationList=baseMapper.selectevaluationbyShopId(shopId);
        return evaluationList;
    }

    @Override
    public List<Evaluation> selectevaluationbyShopIdWithQuery(com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Evaluation> queryWrapper) {
        List<Evaluation> evaluationList = baseMapper.selectevaluationbyShopIdWithQuery(queryWrapper);
        return evaluationList;
    }
}
