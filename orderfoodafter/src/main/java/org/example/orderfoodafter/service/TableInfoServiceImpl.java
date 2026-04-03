package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.TableInfo;
import org.example.orderfoodafter.mapper.TableInfoMapper;

/**
 * 餐桌信息Service实现类
 * 实现餐桌信息相关的业务逻辑处理功能
 *
 * @author 陈逸磊
 * @date 2026-01-22
 */
@Service
public class TableInfoServiceImpl extends ServiceImpl<TableInfoMapper, TableInfo> implements TableInfoService{

}
