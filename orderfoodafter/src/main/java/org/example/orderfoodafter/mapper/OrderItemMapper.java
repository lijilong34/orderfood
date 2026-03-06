package org.example.orderfoodafter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.orderfoodafter.entity.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderItemMapper extends BaseMapper<OrderItem> {
    List<OrderItem> selectorderitembyorderid(@Param("orderid") int orderid);

    List<OrderItem> selectbyorderitembyid(@Param("shopid") int shopid);

    List<OrderItem> selectbyuserid(@Param("userid") int userid);

    List<OrderItem> selectOrderItembylist(@Param("id") int id,@Param("oid") int oid,@Param("name") String name, @Param("phone") String phone);
    
    /**
     * 执行自定义SQL查询
     */
    List<Map<String, Object>> selectMapsBySql(@Param("sql") String sql, @Param("params") Object... params);
}
