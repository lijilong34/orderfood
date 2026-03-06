package org.example.orderfoodafter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.orderfoodafter.entity.Shop;
import org.example.orderfoodafter.entity.Order;
import org.example.orderfoodafter.entity.OrderItem;
import org.example.orderfoodafter.entity.Product;
import org.example.orderfoodafter.mapper.ShopMapper;
import org.example.orderfoodafter.mapper.OrderMapper;
import org.example.orderfoodafter.mapper.OrderItemMapper;
import org.example.orderfoodafter.mapper.ProductMapper;
import org.example.orderfoodafter.mapper.CommonMapper;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService{

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private CommonMapper commonMapper;

    @Override
    public List<Shop> selectcategoryforshop(String categoryname) {
        List<Shop> shops = baseMapper.selectcategoryforshop(categoryname);
        return shops;
    }
    
    @Override
    public List<Long> selectShopIdsByCategory(String categoryname) {
        return baseMapper.selectShopIdsByCategory(categoryname);
    }
    
    @Override
    public List<Shop> selectShopWithCategoriesByIds(List<Long> shopIds) {
        return baseMapper.selectShopWithCategoriesByIds(shopIds);
    }

    @Override
    public List selectShopBytop() {
        List<Shop> shops = baseMapper.selectShopBytop();
        return shops;
    }

    @Override
    public List<Shop> selectShopBylist(int id,int cid,String nickname,String phone) {
        List<Shop> shop=baseMapper.selectShopBylist(id,cid,nickname,phone);
        return shop;
    }

    @Override
    public Map<String, Object> getShopDashboardData(Long shopId) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查shopId是否有效
        if (shopId == null || shopId == 0) {
            result.put("totalOrders", 0);
            result.put("totalRevenue", 0);
            result.put("totalCustomers", 0);
            result.put("orderGrowth", 0);
            result.put("shopName", "");
            return result;
        }

        // 查询店铺信息
        Shop shop = this.getById(shopId);
        if (shop == null) {
            result.put("totalOrders", 0);
            result.put("totalRevenue", 0);
            result.put("totalCustomers", 0);
            result.put("orderGrowth", 0);
            result.put("shopName", "");
            return result;
        }

        // 查询已完成订单（状态为2或4，根据实际业务逻辑调整）
        QueryWrapper<Order> orderQuery = new QueryWrapper<>();
        orderQuery.eq("shop_id", shopId);
        orderQuery.in("status", 0,1,2, 4); // 假设2和4表示已完成订单
        List<Order> orders = orderMapper.selectList(orderQuery);

        // 计算总订单数
        int totalOrders = orders.size();
        
        // 计算总收入
        double totalRevenue = 0;
        for (Order order : orders) {
            if (order.getTotalAmount() != null) {
                totalRevenue += order.getTotalAmount().doubleValue();
            }
        }
        // 格式化为两位小数
        totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;

        // 计算客户数（去重用户ID）
        QueryWrapper<Order> customerQuery = new QueryWrapper<>();
        customerQuery.eq("shop_id", shopId);
        customerQuery.select("DISTINCT user_id");
        List<Order> customerOrders = orderMapper.selectList(customerQuery);
        int totalCustomers = customerOrders.size();

        // 计算订单增长率（与上个月比较，这里简化为示例）
        double orderGrowth = 15.0; // 可以根据实际业务逻辑计算

        result.put("totalOrders", totalOrders);
        result.put("totalRevenue", totalRevenue);
        result.put("totalCustomers", totalCustomers);
        result.put("orderGrowth", orderGrowth);
        result.put("shopName", shop.getName());

        return result;
    }

    @Override
    public List<Map<String, Object>> getHotProducts(Long shopId) {
        List<Map<String, Object>> hotProducts = new ArrayList<>();
        
        // 使用Mapper方法查询热门商品数据
        List<Map<String, Object>> results = baseMapper.selectHotProducts(shopId);
        
        for (Map<String, Object> row : results) {
            Map<String, Object> product = new HashMap<>();
            product.put("name", row.get("name"));
            product.put("sales", row.get("sales"));
            product.put("revenue", row.get("revenue"));
            hotProducts.add(product);
        }

        return hotProducts;
    }

    @Override
    public List<Map<String, Object>> getLatestOrders(Long shopId) {
        List<Map<String, Object>> latestOrders = new ArrayList<>();
        
        // 使用OrderMapper的selectLatestOrdersByShopId查询，该查询已经关联用户表获取昵称且避免了字段歧义
        List<Order> orders = orderMapper.selectLatestOrdersByShopId(shopId);
        
        for (Order order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderId", order.getOrderNo());
            // 使用从数据库查询到的昵称，如果昵称为空则使用"客户" + userId
            String customerName = order.getNickname();
            if (customerName == null || customerName.trim().isEmpty()) {
                customerName = "客户" + order.getUserId();
            }
            orderMap.put("customer", customerName);
            orderMap.put("amount", order.getPayAmount()); // 使用实付金额
            orderMap.put("status", getStatusText(order.getStatus()));
            orderMap.put("createTime", order.getCreateTime());
            latestOrders.add(orderMap);
        }

        return latestOrders;
    }

    @Override
    public Map<String, Object> getShopTrendData(Long shopId) {
        Map<String, Object> trendData = new HashMap<>();
        List<Map<String, Object>> salesTrend = new ArrayList<>();
        List<Map<String, Object>> orderTrend = new ArrayList<>();
        
        // 查询最近7天的订单趋势数据
        List<Map<String, Object>> orderResults = baseMapper.selectShopOrderTrend(shopId);
        // 查询最近7天的销售额趋势数据
        List<Map<String, Object>> salesResults = baseMapper.selectShopSalesTrend(shopId);
        
        // 生成最近7天的日期列表
        List<String> dateList = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
            dateList.add(date.toString());
        }
        
        // 处理订单量趋势数据
        Map<String, Object> orderMap = new HashMap<>();
        for (Map<String, Object> result : orderResults) {
            String date = result.get("date").toString();
            // 确保日期格式一致
            if (date.length() == 10) { // YYYY-MM-DD格式
                orderMap.put(date, result.get("orders"));
            }
        }
        
        for (String date : dateList) {
            Map<String, Object> orderDay = new HashMap<>();
            orderDay.put("date", date);
            Object orders = orderMap.get(date);
            if (orders != null) {
                // 确保返回数值类型
                if (orders instanceof Number) {
                    orderDay.put("orders", ((Number) orders).intValue());
                } else {
                    orderDay.put("orders", Integer.parseInt(orders.toString()));
                }
            } else {
                orderDay.put("orders", 0);
            }
            orderTrend.add(orderDay);
        }
        
        // 处理销售额趋势数据
        Map<String, Object> salesMap = new HashMap<>();
        for (Map<String, Object> result : salesResults) {
            String date = result.get("date").toString();
            // 确保日期格式一致
            if (date.length() == 10) { // YYYY-MM-DD格式
                salesMap.put(date, result.get("sales"));
            }
        }
        
        for (String date : dateList) {
            Map<String, Object> salesDay = new HashMap<>();
            salesDay.put("date", date);
            Object sales = salesMap.get(date);
            if (sales != null) {
                // 确保返回数值类型
                if (sales instanceof Number) {
                    salesDay.put("sales", ((Number) sales).doubleValue());
                } else {
                    salesDay.put("sales", Double.parseDouble(sales.toString()));
                }
            } else {
                salesDay.put("sales", 0.0);
            }
            salesTrend.add(salesDay);
        }
        
        trendData.put("salesTrend", salesTrend);
        trendData.put("orderTrend", orderTrend);
        return trendData;
    }

    @Override
    public List<Shop> selectallandProduct(QueryWrapper<Shop> queryWrapper) {
        List<Shop> shopList = baseMapper.selectallProductbyconut(queryWrapper);
        return shopList;
    }

    @Override
    public List<Shop> selectShopsWithProductsByIds(List<Long> shopIds) {
        return baseMapper.selectShopsWithProductsByIds(shopIds);
    }

    private String getStatusText(int status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "待收货";
            case 2: return "已完成";
            case 3: return "已取消";
            case 4: return "已评价";
            default: return "未知状态";
        }
    }
    
    private String getDayOfWeek(int dayIndex) {
        String[] days = {"日", "一", "二", "三", "四", "五", "六"};
        return days[dayIndex % 7];
    }
}
