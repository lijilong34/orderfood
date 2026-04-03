package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.Inventory;

import java.util.List;
import java.util.Map;

/**
 * 库存Mapper接口
 * 用于对库存表进行数据访问操作，提供库存信息的增删改查功能
 *
 * @author 熊杨博
 * @date 2026-01-12
 */
public interface InventoryMapper extends BaseMapper<Inventory> {
    
    /**
     * 连表查询库存及供应商名称
     * @param params 查询参数
     * @return 库存列表
     */
    List<Inventory> selectInventoryWithSupplier(@Param("params") Map<String, Object> params);
}