package org.example.orderfoodafter.service;

import org.example.orderfoodafter.entity.Inventory;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

/**
 * 库存Service接口
 * 提供库存相关的业务逻辑处理功能，包括库存信息的增删改查等操作
 *
 * @author 熊杨博
 * @date 2026-01-12
 */
public interface InventoryService extends IService<Inventory>{

    /**
     * 连表查询库存及供应商名称
     * @param params 查询参数
     * @return 库存列表
     */
    List<Inventory> selectInventoryWithSupplier(Map<String, Object> params);
}
