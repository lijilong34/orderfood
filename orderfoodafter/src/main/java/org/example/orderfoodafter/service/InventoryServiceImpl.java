package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.mapper.InventoryMapper;
import org.example.orderfoodafter.entity.Inventory;
import org.example.orderfoodafter.service.InventoryService;

/**
 * 库存Service实现类
 * 实现库存相关的业务逻辑处理功能
 *
 * @author 熊杨博
 * @date 2026-01-13
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService{
/**
 * selectInventoryWithSupplier方法
 *
 * @author 熊杨博
 */

    @Override
    public List<Inventory> selectInventoryWithSupplier(Map<String, Object> params) {
        return baseMapper.selectInventoryWithSupplier(params);
    }
}
