// 定义包名为org.example.orderfoodafter.controller
package org.example.orderfoodafter.controller;

// 导入MyBatis Plus的QueryWrapper类，用于构建查询条件
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// 导入项目通用的R类，用于统一响应格式
import org.example.orderfoodafter.common.R;
// 导入Order实体类
import org.example.orderfoodafter.entity.Order;
// 导入Shop实体类
import org.example.orderfoodafter.entity.Shop;
// 导入OrderService服务接口
import org.example.orderfoodafter.service.OrderService;
// 导入ShopService服务接口
import org.example.orderfoodafter.service.ShopService;
// 导入Spring的Autowired注解，用于自动装配
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring MVC的映射注解
import org.springframework.web.bind.annotation.*;
// 导入Jakarta Servlet的HttpServletRequest类
import jakarta.servlet.http.HttpServletRequest;

// 导入Java集合类
import java.util.ArrayList;
// 导入Java日期类Date
import java.util.Date;
// 导入Java集合类HashMap
import java.util.HashMap;
// 导入Java集合类List
import java.util.List;
// 导入Java集合类Map
import java.util.Map;

/**
 * 店铺管理控制器
 * 负责管理店铺的基本信息和运营状态，包括店铺的创建、查询、营业状态管理、营业额统计等功能
 *
 * @author 周子金
 * @date 2026-03-18
 */
// 使用RestController注解标记该类为RESTful控制器，所有方法返回JSON
@RestController
// 使用RequestMapping注解设置该控制器的基础请求路径为/shop
@RequestMapping("/shop")
// 定义ShopController类，继承自BaseController基类，泛型为Shop和ShopService

public class ShopController extends BaseController<Shop, ShopService> {

    // 使用Autowired注解自动注入OrderService服务实例
    @Autowired
    // 声明OrderService服务对象
    private OrderService orderService;

    /**
     * 构造函数
     * @param service 店铺服务
     * @author 周子金
     */
    @Autowired
    public ShopController(ShopService service) {
        // 调用父类BaseController的构造函数，传入service
        super(service);
    }

    /**
     * 暂停营业
     * @param id 店铺ID
     * @return 操作结果
     * @author 周子金
     */
    // 使用PostMapping注解映射POST请求到/pause/{id}路径
    @PostMapping("/pause/{id}")
    public R pauseShop(@PathVariable("id") Long id) {
        // 使用try-catch块捕获异常
        try {
            // 根据ID查询店铺信息
            Shop shop = service.getById(id);
            // 判断店铺是否存在
            if (shop != null) {
                // 设置店铺状态为0（暂停营业）
                shop.setStatus((byte) 0);
                // 更新店铺信息
                service.updateById(shop);
                // 返回成功响应，包含暂停营业状态
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "店铺已暂停营业");
            }
            // 如果店铺不存在，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("店铺不存在");
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常并包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("暂停营业失败: " + e.getMessage());
        }
    }

    /**
     * 开启营业
     * @param id 店铺ID
     * @return 操作结果
     * @author 周子金
     */
    // 使用PostMapping注解映射POST请求到/open/{id}路径
    @PostMapping("/open/{id}")
    public R openShop(@PathVariable("id") Long id) {
        // 使用try-catch块捕获异常
        try {
            // 根据ID查询店铺信息
            Shop shop = service.getById(id);
            // 判断店铺是否存在
            if (shop != null) {
                // 设置店铺状态为1（正常营业）
                shop.setStatus((byte) 1);
                // 更新店铺信息
                service.updateById(shop);
                // 返回成功响应，包含开启营业状态
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "店铺已开启营业");
            }
            // 如果店铺不存在，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("店铺不存在");
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常并包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("开启营业失败: " + e.getMessage());
        }
    }

    /**
     * 封禁店铺
     * @param id 店铺ID
     * @return 操作结果
     * @author 周子金
     */
    // 使用PostMapping注解映射POST请求到/ban/{id}路径
    @PostMapping("/ban/{id}")
    public R banShop(@PathVariable("id") Long id) {
        // 使用try-catch块捕获异常
        try {
            // 根据ID查询店铺信息
            Shop shop = service.getById(id);
            // 判断店铺是否存在
            if (shop != null) {
                // 设置店铺状态为2（已封禁）
                shop.setStatus((byte) 2);
                // 更新店铺信息
                service.updateById(shop);
                // 返回成功响应，包含封禁状态
                    /**
     * R
     * 
     * @author 李梦瑶
     */
                return new R().addData("status", "店铺已封禁");
            }
            // 如果店铺不存在，抛出运行时异常
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("店铺不存在");
        } catch (Exception e) {
            // 捕获到异常时，抛出运行时异常并包含错误信息
                /**
     * RuntimeException
     * 
     * @author 李梦瑶
     */
            throw new RuntimeException("封禁店铺失败: " + e.getMessage());
        }
    }

    /**
     * 查看店铺营业额
     * @param id 店铺ID
     * @return 营业额信息
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/revenue/{id}路径
    @GetMapping("/revenue/{id}")
    public R getShopRevenue(@PathVariable("id") Long id) {
        // 使用try-catch块捕获异常
        try {
            // 查询店铺信息
            Shop shop = service.getById(id);
            // 判断店铺是否存在
            if (shop == null) {
                // 如果店铺不存在，返回错误响应
                return R.error("店铺不存在");
            }

            // 查询店铺的已完成订单（状态0,1,2,4）
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            // 设置查询条件：店铺ID等于传入的id
            queryWrapper.eq("shop_id", id);
            // 设置查询条件：订单状态在0,1,2,4中
            queryWrapper.in("status", 0,1,2,4);
            // 按创建时间倒序排列
            queryWrapper.orderByDesc("create_time");
            // 查询订单列表
            List<Order> orders = orderService.list(queryWrapper);

            // 计算总营业额，处理字符串格式的金额
            double totalRevenue = 0;
            // 遍历订单列表
            for (Order order : orders) {
                // 判断订单金额是否不为空
                if (order.getTotalAmount() != null) {
                    // 使用try-catch块处理金额解析
                    try {
                        // 如果金额是字符串，转换为double并保留两位小数
                        String amountStr = order.getTotalAmount().toString();
                        // 将金额字符串转换为double类型
                        double amount = Double.parseDouble(amountStr);
                        // 累加到总营业额，保留两位小数
                        totalRevenue += Math.round(amount * 100.0) / 100.0;
                    } catch (NumberFormatException e) {
                        // 如果金额格式错误，在标准错误输出中打印错误信息
                        System.err.println("金额格式错误: " + order.getTotalAmount());
                    }
                }
            }

            // 计算客单价，保留两位小数
            double avgOrderValue = orders.size() > 0 ? Math.round((totalRevenue / orders.size()) * 100.) / 100 : 0;

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            // 存储店铺ID
            result.put("shopId", id);
            // 存储店铺名称
            result.put("shopName", shop.getName());
            // 存储总营业额
            result.put("revenue", totalRevenue);
            // 存储订单数量
            result.put("orderCount", orders.size());
            // 存储客单价
            result.put("avgOrderValue", avgOrderValue);
            // 存储时间范围描述
            result.put("time", "总营业额");
            // 存储订单列表（空列表）
            result.put("orders", new ArrayList<>());

            // 创建成功的响应对象
            R response = R.ok();
            // 设置响应消息
            response.setMessage("查询成功");
            // 添加数据到响应中
            response.addData("entity", result);
            // 返回响应对象
            return response;

        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("查询营业额失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有店铺的营业额数据（用于图表显示，自动排除状态3的店铺）
     * @return 所有店铺的营业额数据
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/revenue-all-for-chart路径
    @GetMapping("/revenue-all-for-chart")
    public R getAllShopsRevenueForChart() {
        // 使用try-catch块捕获异常
        try {
            // 查询所有非状态3的店铺
            QueryWrapper<Shop> shopQueryWrapper = new QueryWrapper<>();
            // 设置查询条件：店铺状态不等于3
            shopQueryWrapper.ne("status", 3);
            // 查询店铺列表
            List<Shop> shops = service.list(shopQueryWrapper);

            // 创建结果列表
            List<Map<String, Object>> results = new ArrayList<>();

            // 遍历店铺列表
            for (Shop shop : shops) {
                // 查询每个店铺的已完成订单（状态2）
                QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
                // 设置查询条件：店铺ID等于当前店铺ID
                orderQueryWrapper.eq("shop_id", shop.getId());
                // 查询订单列表
                List<Order> orders = orderService.list(orderQueryWrapper);

                // 计算总营业额
                double totalRevenue = 0;
                // 遍历订单列表
                for (Order order : orders) {
                    // 判断订单金额是否不为空
                    if (order.getTotalAmount() != null) {
                        // 使用try-catch块处理金额解析
                        try {
                            // 如果金额是字符串，转换为double并保留两位小数
                            String amountStr = order.getTotalAmount().toString();
                            // 将金额字符串转换为double类型
                            double amount = Double.parseDouble(amountStr);
                            // 累加到总营业额，保留两位小数
                            totalRevenue += Math.round(amount * 100.0) / 100.0;
                        } catch (NumberFormatException e) {
                            // 如果金额格式错误，在标准错误输出中打印错误信息
                            System.err.println("金额格式错误: " + order.getTotalAmount());
                        }
                    }
                }

                // 计算客单价
                double avgOrderValue = orders.size() > 0 ? Math.round((totalRevenue / orders.size()) * 100.) / 100 : 0;

                // 构建返回数据
                Map<String, Object> result = new HashMap<>();
                // 存储店铺ID
                result.put("shopId", shop.getId());
                // 存储店铺名称
                result.put("shopName", shop.getName());
                // 存储总营业额
                result.put("revenue", totalRevenue);
                // 存储订单数量
                result.put("orderCount", orders.size());
                // 存储客单价
                result.put("avgOrderValue", avgOrderValue);
                // 存储时间范围描述
                result.put("time", "总营业额");

                // 将结果添加到结果列表中
                results.add(result);
            }

            // 创建成功的响应对象
            R response = R.ok();
            // 设置响应消息
            response.setMessage("查询成功");
            // 添加数据到响应中
            response.addData("shops", results);
            // 添加店铺总数到响应中
            response.addData("totalShops", shops.size());
            // 返回响应对象
            return response;

        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("查询营业额失败: " + e.getMessage());
        }
    }

    /**
     * 查看店铺日销售额
     * @param id 店铺ID
     * @param date 日期
     * @return 日销售额信息
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/daily-revenue/{id}路径
    @GetMapping("/daily-revenue/{id}")
    public R getDailyRevenue(@PathVariable("id") Long id, @RequestParam String date) {
        // 创建查询条件构建器
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        // 设置查询条件：店铺ID等于传入的id
        queryWrapper.eq("shop_id", id);
        // 设置查询条件：订单状态等于3（已完成）
        queryWrapper.eq("status", 3);
        // 设置查询条件：创建时间包含指定的日期
        queryWrapper.like("create_time", date);
        // 查询订单列表
        List<Order> orders = orderService.list(queryWrapper);

        // 计算日销售额
        double dailyRevenue = 0;
        // 遍历订单列表
        for (Order order : orders) {
            // 累加订单金额到日销售额
            dailyRevenue += order.getTotalAmount().doubleValue();
        }

        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        // 存储日期
        result.put("date", date);
        // 存储日销售额
        result.put("dailyRevenue", dailyRevenue);
        // 存储订单数量
        result.put("orderCount", orders.size());

        // 返回成功响应，包含日销售额信息
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("dailyInfo", result);
    }

    /**
     * 查看店铺月销售额
     * @param id 店铺ID
     * @param month 月份
     * @return 月销售额信息
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/monthly-revenue/{id}路径
    @GetMapping("/monthly-revenue/{id}")
    public R getMonthlyRevenue(@PathVariable("id") Long id, @RequestParam String month) {
        // 创建查询条件构建器
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        // 设置查询条件：店铺ID等于传入的id
        queryWrapper.eq("shop_id", id);
        // 设置查询条件：订单状态在0,1,2,4中（假设3表示订单已完成）
        queryWrapper.in("status", 0,1,2,4);
        // 设置查询条件：创建时间包含指定的月份
        queryWrapper.like("create_time", month);
        // 查询订单列表
        List<Order> orders = orderService.list(queryWrapper);

        // 计算月销售额
        double monthlyRevenue = 0;
        // 遍历订单列表
        for (Order order : orders) {
            // 累加订单金额到月销售额
            monthlyRevenue += order.getTotalAmount().doubleValue();
        }
        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        // 存储月份
        result.put("month", month);
        // 存储月销售额
        result.put("monthlyRevenue", monthlyRevenue);
        // 存储订单数量
        result.put("orderCount", orders.size());

        // 返回成功响应，包含月销售额信息
            /**
     * R
     * 
     * @author 李梦瑶
     */
        return new R().addData("monthlyInfo", result);
    }
    /**
     * 根据店铺名称和手机号查询店铺
     * @param shop 店铺对象
     * @return 店铺信息
     * @author 周子金
     */
    // 使用PostMapping注解映射POST请求到/selectshopbyshop路径
    @PostMapping("/selectshopbyshop")
    public R selectShopByShop(@RequestBody Shop shop) {
        // 创建查询条件构建器
       QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
       // 设置查询条件：店铺名称等于传入的名称
       queryWrapper.eq("name", shop.getName());
       // 设置查询条件：店铺手机号等于传入的手机号
       queryWrapper.eq("phone",shop.getPhone());
       // 查询单个店铺对象
       Shop shop1=service.getOne(queryWrapper);
       // 返回成功响应，包含店铺信息
           /**
     * R
     * 
     * @author 李梦瑶
     */
       return new R().addData("shop",shop1);
    }

    /**
     * 获取店铺仪表板数据
     * @param shopIdStr 店铺ID字符串
     * @return 仪表板数据
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/dashboard路径
    @GetMapping("/dashboard")
    public R getShopDashboardData(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        // 使用try-catch块捕获异常
        try {
            // 声明店铺ID变量
            Long shopId = null;
            // 判断shopIdStr是否不为空且不是空白字符串
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                // 使用try-catch块解析店铺ID
                try {
                    // 将shopIdStr转换为Long类型
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    // 如果格式错误，返回错误响应
                    return R.error("店铺ID格式错误");
                }
            }
            // 判断店铺ID是否为空或等于0
            if (shopId == null || shopId == 0) {
                // 如果店铺ID为空，返回错误响应
                return R.error("店铺ID不能为空");
            }
            // 调用service层获取店铺仪表板数据
            Map<String, Object> dashboardData = service.getShopDashboardData(shopId);
            // 返回成功响应，包含仪表板数据
            return R.ok().addData("data", dashboardData);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取仪表板数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门商品数据
     * @param shopIdStr 店铺ID字符串
     * @return 热门商品数据
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/hot-products路径
    @GetMapping("/hot-products")
    public R getHotProducts(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        // 使用try-catch块捕获异常
        try {
            // 声明店铺ID变量
            Long shopId = null;
            // 判断shopIdStr是否不为空且不是空白字符串
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                // 使用try-catch块解析店铺ID
                try {
                    // 将shopIdStr转换为Long类型
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    // 如果格式错误，返回错误响应
                    return R.error("店铺ID格式错误");
                }
            }
            // 判断店铺ID是否为空或等于0
            if (shopId == null || shopId == 0) {
                // 如果店铺ID为空，返回错误响应
                return R.error("店铺ID不能为空");
            }
            // 调用service层获取热门商品数据
            List<Map<String, Object>> hotProducts = service.getHotProducts(shopId);
            // 创建结果Map
            Map<String, Object> result = new HashMap<>();
            // 存储热门商品列表
            result.put("products", hotProducts);
            // 返回成功响应，包含热门商品数据
            return R.ok().addData("data", result);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取热门商品数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新订单数据
     * @param shopIdStr 店铺ID字符串
     * @return 最新订单数据
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/latest-orders路径
    @GetMapping("/latest-orders")
    public R getLatestOrders(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        // 使用try-catch块捕获异常
        try {
            // 声明店铺ID变量
            Long shopId = null;
            // 判断shopIdStr是否不为空且不是空白字符串
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                // 使用try-catch块解析店铺ID
                try {
                    // 将shopIdStr转换为Long类型
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    // 如果格式错误，返回错误响应
                    return R.error("店铺ID格式错误");
                }
            }
            // 判断店铺ID是否为空或等于0
            if (shopId == null || shopId == 0) {
                // 如果店铺ID为空，返回错误响应
                return R.error("店铺ID不能为空");
            }
            // 调用service层获取最新订单数据
            List<Map<String, Object>> latestOrders = service.getLatestOrders(shopId);
            // 创建结果Map
            Map<String, Object> result = new HashMap<>();
            // 存储最新订单列表
            result.put("orders", latestOrders);
            // 返回成功响应，包含最新订单数据
            return R.ok().addData("data", result);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取最新订单数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取店铺趋势数据
     * @param shopIdStr 店铺ID字符串
     * @return 店铺趋势数据
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/trend-data路径
    @GetMapping("/trend-data")
    public R getShopTrendData(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        // 使用try-catch块捕获异常
        try {
            // 声明店铺ID变量
            Long shopId = null;
            // 判断shopIdStr是否不为空且不是空白字符串
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                // 使用try-catch块解析店铺ID
                try {
                    // 将shopIdStr转换为Long类型
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    // 如果格式错误，返回错误响应
                    return R.error("店铺ID格式错误");
                }
            }
            // 判断店铺ID是否为空或等于0
            if (shopId == null || shopId == 0) {
                // 如果店铺ID为空，返回错误响应
                return R.error("店铺ID不能为空");
            }
            // 调用service层获取店铺趋势数据
            Map<String, Object> trendData = service.getShopTrendData(shopId);
            // 返回成功响应，包含趋势数据
            return R.ok().addData("data", trendData);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取店铺销售统计数据
     * @param shopIdStr 店铺ID字符串
     * @return 销售统计数据
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/sales-stats路径
    @GetMapping("/sales-stats")
    public R getShopSalesStats(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        // 使用try-catch块捕获异常
        try {
            // 声明店铺ID变量
            Long shopId = null;
            // 判断shopIdStr是否不为空且不是空白字符串
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                // 使用try-catch块解析店铺ID
                try {
                    // 将shopIdStr转换为Long类型
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    // 如果格式错误，返回错误响应
                    return R.error("店铺ID格式错误");
                }
            }
            // 判断店铺ID是否为空或等于0
            if (shopId == null || shopId == 0) {
                // 如果店铺ID为空，返回错误响应
                return R.error("店铺ID不能为空");
            }
            // 调用service层获取店铺仪表板数据（销售统计）
            Map<String, Object> dashboardData = service.getShopDashboardData(shopId);
            // 返回成功响应，包含销售统计数据
            return R.ok().addData("data", dashboardData);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取销售统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取店铺订单统计数据
     * @param shopIdStr 店铺ID字符串
     * @return 订单统计数据
     * @author 周子金
     */
    // 使用GetMapping注解映射GET请求到/order-stats路径
    @GetMapping("/order-stats")
    public R getShopOrderStats(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        // 使用try-catch块捕获异常
        try {
            // 声明店铺ID变量
            Long shopId = null;
            // 判断shopIdStr是否不为空且不是空白字符串
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                // 使用try-catch块解析店铺ID
                try {
                    // 将shopIdStr转换为Long类型
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    // 如果格式错误，返回错误响应
                    return R.error("店铺ID格式错误");
                }
            }
            // 判断店铺ID是否为空或等于0
            if (shopId == null || shopId == 0) {
                // 如果店铺ID为空，返回错误响应
                return R.error("店铺ID不能为空");
            }
            // 调用service层获取店铺仪表板数据（订单统计）
            Map<String, Object> dashboardData = service.getShopDashboardData(shopId);
            // 返回成功响应，包含订单统计数据
            return R.ok().addData("data", dashboardData);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 返回错误响应，包含错误信息
            return R.error("获取订单统计数据失败: " + e.getMessage());
        }
    }

	/**
	 * 获取店铺营业额排行榜
	 * @return 营业额排行榜数据
	 * @author 周子金
	 */
	// 使用GetMapping注解映射GET请求到/rankings/revenue路径
	@GetMapping("/rankings/revenue")
	public R getRevenueRankings() {
		// 使用try-catch块捕获异常
		try {
			// 查询所有非状态3的店铺
			QueryWrapper<Shop> shopQueryWrapper = new QueryWrapper<>();
			// 设置查询条件：店铺状态不等于3
			shopQueryWrapper.ne("status", 3);
			// 查询店铺列表
			List<Shop> shops = service.list(shopQueryWrapper);

			// 创建结果列表
			List<Map<String, Object>> results = new ArrayList<>();

			// 遍历店铺列表
			for (Shop shop : shops) {
				// 查询每个店铺的已完成订单
				QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
				// 设置查询条件：店铺ID等于当前店铺ID
				orderQueryWrapper.eq("shop_id", shop.getId());
				// 设置查询条件：订单状态在0,1,2,4中
				orderQueryWrapper.in("status", 0, 1, 2, 4);
				// 查询订单列表
				List<Order> orders = orderService.list(orderQueryWrapper);

				// 计算总营业额
				double totalRevenue = 0;
				// 遍历订单列表
				for (Order order : orders) {
					// 判断订单金额是否不为空
					if (order.getTotalAmount() != null) {
						// 使用try-catch块处理金额解析
						try {
							// 如果金额是字符串，转换为double并保留两位小数
							String amountStr = order.getTotalAmount().toString();
							// 将金额字符串转换为double类型
							double amount = Double.parseDouble(amountStr);
							// 累加到总营业额，保留两位小数
							totalRevenue += Math.round(amount * 100.0) / 100.0;
						} catch (NumberFormatException e) {
							// 如果金额格式错误，在标准错误输出中打印错误信息
							System.err.println("金额格式错误: " + order.getTotalAmount());
						}
					}
				}

				// 计算客单价
				double avgOrderValue = orders.size() > 0 ? Math.round((totalRevenue / orders.size()) * 100.) / 100 : 0;

				// 构建返回数据
				Map<String, Object> result = new HashMap<>();
				// 存储店铺ID
				result.put("shopId", shop.getId());
				// 存储店铺名称
				result.put("shopName", shop.getName());
				// 存储店铺Logo
				result.put("logo", shop.getLogo());
				// 存储总营业额
				result.put("revenue", totalRevenue);
				// 存储订单数量
				result.put("orderCount", orders.size());
				// 存储客单价
				result.put("avgOrderValue", avgOrderValue);
				// 存储店铺状态
				result.put("status", shop.getStatus());
				// 存储店铺创建时间
				result.put("createTime", shop.getCreateTime());

				// 将结果添加到结果列表中
				results.add(result);
			}

			// 按营业额排序
			results.sort((a, b) -> {
				// 获取第一个结果的营业额
				Double revenueA = (Double) a.get("revenue");
				// 获取第二个结果的营业额
				Double revenueB = (Double) b.get("revenue");
				// 返回营业额的比较结果（降序）
				return revenueB.compareTo(revenueA);
			});

			// 创建成功的响应对象
			R response = R.ok();
			// 设置响应消息
			response.setMessage("查询成功");
			// 添加数据到响应中
			response.addData("shops", results);
			// 返回响应对象
			return response;

		} catch (Exception e) {
			// 打印异常堆栈信息
			e.printStackTrace();
			// 返回错误响应，包含错误信息
			return R.error("查询营业额排行榜失败: " + e.getMessage());
		}
	}

	/**
	 * 获取店铺订单数排行榜
	 * @return 订单数排行榜数据
	 * @author 周子金
	 */
	// 使用GetMapping注解映射GET请求到/rankings/orders路径
	@GetMapping("/rankings/orders")
	public R getOrderRankings() {
		// 使用try-catch块捕获异常
		try {
			// 查询所有非状态3的店铺
			QueryWrapper<Shop> shopQueryWrapper = new QueryWrapper<>();
			// 设置查询条件：店铺状态不等于3
			shopQueryWrapper.ne("status", 3);
			// 查询店铺列表
			List<Shop> shops = service.list(shopQueryWrapper);

			// 创建结果列表
			List<Map<String, Object>> results = new ArrayList<>();

			// 遍历店铺列表
			for (Shop shop : shops) {
				// 查询每个店铺的订单数
				QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
				// 设置查询条件：店铺ID等于当前店铺ID
				orderQueryWrapper.eq("shop_id", shop.getId());
				// 查询订单数量
				long orderCount = orderService.count(orderQueryWrapper);

				// 查询已完成订单计算营业额
				QueryWrapper<Order> completedOrderQueryWrapper = new QueryWrapper<>();
				// 设置查询条件：店铺ID等于当前店铺ID
				completedOrderQueryWrapper.eq("shop_id", shop.getId());
				// 设置查询条件：订单状态在0,1,2,4中
				completedOrderQueryWrapper.in("status", 0, 1, 2, 4);
				// 查询已完成订单列表
				List<Order> completedOrders = orderService.list(completedOrderQueryWrapper);

				// 计算总营业额
				double totalRevenue = 0;
				// 遍历已完成订单列表
				for (Order order : completedOrders) {
					// 判断订单金额是否不为空
					if (order.getTotalAmount() != null) {
						// 使用try-catch块处理金额解析
						try {
							// 如果金额是字符串，转换为double并保留两位小数
							String amountStr = order.getTotalAmount().toString();
							// 将金额字符串转换为double类型
							double amount = Double.parseDouble(amountStr);
							// 累加到总营业额，保留两位小数
							totalRevenue += Math.round(amount * 100.0) / 100.0;
						} catch (NumberFormatException e) {
							// 如果金额格式错误，在标准错误输出中打印错误信息
							System.err.println("金额格式错误: " + order.getTotalAmount());
						}
					}
				}

				// 计算客单价
				double avgOrderValue = completedOrders.size() > 0 ? Math.round((totalRevenue / completedOrders.size()) * 100.) / 100 : 0;

				// 构建返回数据
				Map<String, Object> result = new HashMap<>();
				// 存储店铺ID
				result.put("shopId", shop.getId());
				// 存储店铺名称
				result.put("shopName", shop.getName());
				// 存储店铺Logo
				result.put("logo", shop.getLogo());
				// 存储总营业额
				result.put("revenue", totalRevenue);
				// 存储订单数量
				result.put("orderCount", orderCount);
				// 存储客单价
				result.put("avgOrderValue", avgOrderValue);
				// 存储店铺状态
				result.put("status", shop.getStatus());
				// 存储店铺创建时间
				result.put("createTime", shop.getCreateTime());

				// 将结果添加到结果列表中
				results.add(result);
			}

			// 按订单数排序
			results.sort((a, b) -> {
				// 获取第一个结果的订单数量
				Long countA = (Long) a.get("orderCount");
				// 获取第二个结果的订单数量
				Long countB = (Long) b.get("orderCount");
				// 返回订单数量的比较结果（降序）
				return countB.compareTo(countA);
			});

			// 创建成功的响应对象
			R response = R.ok();
			// 设置响应消息
			response.setMessage("查询成功");
			// 添加数据到响应中
			response.addData("shops", results);
			// 返回响应对象
			return response;

		} catch (Exception e) {
			// 打印异常堆栈信息
			e.printStackTrace();
			// 返回错误响应，包含错误信息
			return R.error("查询订单数排行榜失败: " + e.getMessage());
		}
	}

	/**
	 * 获取店铺评分排行榜
	 * @return 评分排行榜数据
	 * @author 周子金
	 */
	// 使用GetMapping注解映射GET请求到/rankings/rating路径
	@GetMapping("/rankings/rating")
	public R getRatingRankings() {
		// 使用try-catch块捕获异常
		try {
			// 查询所有非状态3的店铺
			QueryWrapper<Shop> shopQueryWrapper = new QueryWrapper<>();
			// 设置查询条件：店铺状态不等于3
			shopQueryWrapper.ne("status", 3);
			// 查询店铺列表
			List<Shop> shops = service.list(shopQueryWrapper);

			// 创建结果列表
			List<Map<String, Object>> results = new ArrayList<>();

			// 遍历店铺列表
			for (Shop shop : shops) {
				// 计算订单数和营业额
				QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
				// 设置查询条件：店铺ID等于当前店铺ID
				orderQueryWrapper.eq("shop_id", shop.getId());
				// 设置查询条件：订单状态在0,1,2,4中
				orderQueryWrapper.in("status", 0, 1, 2, 4);
				// 查询订单列表
				List<Order> orders = orderService.list(orderQueryWrapper);

				// 计算总营业额
				double totalRevenue = 0;
				// 遍历订单列表
				for (Order order : orders) {
					// 判断订单金额是否不为空
					if (order.getTotalAmount() != null) {
						// 使用try-catch块处理金额解析
						try {
							// 如果金额是字符串，转换为double并保留两位小数
							String amountStr = order.getTotalAmount().toString();
							// 将金额字符串转换为double类型
							double amount = Double.parseDouble(amountStr);
							// 累加到总营业额，保留两位小数
							totalRevenue += Math.round(amount * 100.0) / 100.0;
						} catch (NumberFormatException e) {
							// 如果金额格式错误，在标准错误输出中打印错误信息
							System.err.println("金额格式错误: " + order.getTotalAmount());
						}
					}
				}

				// 计算客单价
				double avgOrderValue = orders.size() > 0 ? Math.round((totalRevenue / orders.size()) * 100.) / 100 : 0;

				// 构建返回数据
				Map<String, Object> result = new HashMap<>();
				// 存储店铺ID
				result.put("shopId", shop.getId());
				// 存储店铺名称
				result.put("shopName", shop.getName());
				// 存储店铺Logo
				result.put("logo", shop.getLogo());
				// 存储总营业额
				result.put("revenue", totalRevenue);
				// 存储订单数量
				result.put("orderCount", orders.size());
				// 存储客单价
				result.put("avgOrderValue", avgOrderValue);
				// 存储店铺状态
				result.put("status", shop.getStatus());
				// 存储店铺创建时间
				result.put("createTime", shop.getCreateTime());
				// 默认评分，实际应该从评价表计算
				result.put("rating", 4.5);

				// 将结果添加到结果列表中
				results.add(result);
			}

			// 按评分排序
			results.sort((a, b) -> {
				// 获取第一个结果的评分
				Double ratingA = (Double) a.get("rating");
				// 获取第二个结果的评分
				Double ratingB = (Double) b.get("rating");
				// 返回评分的比较结果（降序）
				return ratingB.compareTo(ratingA);
			});

			// 创建成功的响应对象
			R response = R.ok();
			// 设置响应消息
			response.setMessage("查询成功");
			// 添加数据到响应中
			response.addData("shops", results);
			// 返回响应对象
			return response;

		} catch (Exception e) {
			// 打印异常堆栈信息
			e.printStackTrace();
			// 返回错误响应，包含错误信息
			return R.error("查询评分排行榜失败: " + e.getMessage());
		}
	}
}