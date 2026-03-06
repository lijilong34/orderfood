package org.example.orderfoodafter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.entity.Order;
import org.example.orderfoodafter.entity.Shop;
import org.example.orderfoodafter.service.OrderService;
import org.example.orderfoodafter.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺管理控制器
 */
@RestController
@RequestMapping("/shop")
public class ShopController extends BaseController<Shop, ShopService> {

    @Autowired
    private OrderService orderService;

    @Autowired
    public ShopController(ShopService service) {
        super(service);
    }

    /**
     * 暂停营业
     */
    @PostMapping("/pause/{id}")
    public R pauseShop(@PathVariable("id") Long id) {
        Shop shop = service.getById(id);
        if (shop != null) {
            shop.setStatus((byte) 0); // 2表示暂停营业
            service.updateById(shop);
            R r = new R();
            r.setCode(200);
            r.setMessage("店铺已暂停营业");
            return r;
        }
        R r = new R();
        r.setCode(500);
        r.setMessage("店铺不存在");
        return r;
    }

    /**
     * 开启营业
     */
    @PostMapping("/open/{id}")
    public R openShop(@PathVariable("id") Long id) {
        Shop shop = service.getById(id);
        if (shop != null) {
            shop.setStatus((byte) 1); // 1表示正常营业
            service.updateById(shop);
            R r = new R();
            r.setCode(200);
            r.setMessage("店铺已开启营业");
            return r;
        }
        R r = new R();
        r.setCode(500);
        r.setMessage("店铺不存在");
        return r;
    }

    /**
     * 封禁店铺
     */
    @PostMapping("/ban/{id}")
    public R banShop(@PathVariable("id") Long id) {
        Shop shop = service.getById(id);
        if (shop != null) {
            shop.setStatus((byte) 2); // 3表示已封禁
            service.updateById(shop);
            R r = new R();
            r.setCode(200);
            r.setMessage("店铺已封禁");
            return r;
        }
        R r = new R();
        r.setCode(500);
        r.setMessage("店铺不存在");
        return r;
    }

    /**
     * 查看店铺营业额
     */
    @GetMapping("/revenue/{id}")
    public R getShopRevenue(@PathVariable("id") Long id) {
        try {
            // 查询店铺信息
            Shop shop = service.getById(id);
            if (shop == null) {
                return R.error("店铺不存在");
            }

            // 查询店铺的已完成订单（状态2）
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("shop_id", id);
            queryWrapper.in("status", 0,1,2,4); // 只查询状态2的订单（已完成）
            queryWrapper.orderByDesc("create_time");
            List<Order> orders = orderService.list(queryWrapper);

            // 计算总营业额，处理字符串格式的金额
            double totalRevenue = 0;
            for (Order order : orders) {
                if (order.getTotalAmount() != null) {
                    try {
                        // 如果金额是字符串，转换为double并保留两位小数
                        String amountStr = order.getTotalAmount().toString();
                        double amount = Double.parseDouble(amountStr);
                        totalRevenue += Math.round(amount * 100.0) / 100.0; // 保留两位小数
                    } catch (NumberFormatException e) {
                        System.err.println("金额格式错误: " + order.getTotalAmount());
                    }
                }
            }

            // 计算客单价，保留两位小数
            double avgOrderValue = orders.size() > 0 ? Math.round((totalRevenue / orders.size()) * 100.) / 100 : 0;

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("shopId", id);
            result.put("shopName", shop.getName());
            result.put("revenue", totalRevenue);
            result.put("orderCount", orders.size());
            result.put("avgOrderValue", avgOrderValue);
            result.put("time", "总营业额");
            result.put("orders", new ArrayList<>());

            R response = R.ok();
            response.setMessage("查询成功");
            response.addData("entity", result);
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询营业额失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有店铺的营业额数据（用于图表显示，自动排除状态3的店铺）
     */
    @GetMapping("/revenue-all-for-chart")
    public R getAllShopsRevenueForChart() {
        try {
            // 查询所有非状态3的店铺
            QueryWrapper<Shop> shopQueryWrapper = new QueryWrapper<>();
            shopQueryWrapper.ne("status", 3);
            List<Shop> shops = service.list(shopQueryWrapper);
            
            List<Map<String, Object>> results = new ArrayList<>();
            
            for (Shop shop : shops) {
                // 查询每个店铺的已完成订单（状态2）
                QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
                orderQueryWrapper.eq("shop_id", shop.getId());
                List<Order> orders = orderService.list(orderQueryWrapper);
                
                // 计算总营业额
                double totalRevenue = 0;
                for (Order order : orders) {
                    if (order.getTotalAmount() != null) {
                        try {
                            String amountStr = order.getTotalAmount().toString();
                            double amount = Double.parseDouble(amountStr);
                            totalRevenue += Math.round(amount * 100.0) / 100.0;
                        } catch (NumberFormatException e) {
                            System.err.println("金额格式错误: " + order.getTotalAmount());
                        }
                    }
                }
                
                // 计算客单价
                double avgOrderValue = orders.size() > 0 ? Math.round((totalRevenue / orders.size()) * 100.) / 100 : 0;
                
                // 构建返回数据
                Map<String, Object> result = new HashMap<>();
                result.put("shopId", shop.getId());
                result.put("shopName", shop.getName());
                result.put("revenue", totalRevenue);
                result.put("orderCount", orders.size());
                result.put("avgOrderValue", avgOrderValue);
                result.put("time", "总营业额");
                
                results.add(result);
            }
            
            R response = R.ok();
            response.setMessage("查询成功");
            response.addData("shops", results);
            response.addData("totalShops", shops.size());
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询营业额失败: " + e.getMessage());
        }
    }

    /**
     * 查看店铺日销售额
     */
    @GetMapping("/daily-revenue/{id}")
    public R getDailyRevenue(@PathVariable("id") Long id, @RequestParam String date) {
        // 查询指定日期的已完成订单
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", id);
        queryWrapper.eq("status", 3);
        queryWrapper.like("create_time", date);
        List<Order> orders = orderService.list(queryWrapper);

        // 计算日销售额
        double dailyRevenue = 0;
        for (Order order : orders) {
            dailyRevenue += order.getTotalAmount().doubleValue();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("dailyRevenue", dailyRevenue);
        result.put("orderCount", orders.size());

        return new R().addData("dailyInfo", result);
    }

    /**
     * 查看店铺月销售额
     */
    @GetMapping("/monthly-revenue/{id}")
    public R getMonthlyRevenue(@PathVariable("id") Long id, @RequestParam String month) {
        // 查询指定月份的已完成订单
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", id);
        queryWrapper.in("status", 0,1,2,4); // 假设3表示订单已完成
        queryWrapper.like("create_time", month);
        List<Order> orders = orderService.list(queryWrapper);

        // 计算月销售额
        double monthlyRevenue = 0;
        for (Order order : orders) {
            monthlyRevenue += order.getTotalAmount().doubleValue();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("month", month);
        result.put("monthlyRevenue", monthlyRevenue);
        result.put("orderCount", orders.size());

        return new R().addData("monthlyInfo", result);
    }
    @PostMapping("/selectshopbyshop")
    public R selectShopByShop(@RequestBody Shop shop) {
       QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
       queryWrapper.eq("name", shop.getName());
       queryWrapper.eq("phone",shop.getPhone());
       Shop shop1=service.getOne(queryWrapper);
       return new R().addData("shop",shop1);
    }

    /**
     * 获取店铺仪表板数据
     */
    @GetMapping("/dashboard")
    public R getShopDashboardData(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        try {
            Long shopId = null;
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                try {
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    return R.error("店铺ID格式错误");
                }
            }
            if (shopId == null || shopId == 0) {
                return R.error("店铺ID不能为空");
            }
            Map<String, Object> dashboardData = service.getShopDashboardData(shopId);
            return R.ok().addData("data", dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取仪表板数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门商品数据
     */
    @GetMapping("/hot-products")
    public R getHotProducts(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        try {
            Long shopId = null;
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                try {
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    return R.error("店铺ID格式错误");
                }
            }
            if (shopId == null || shopId == 0) {
                return R.error("店铺ID不能为空");
            }
            List<Map<String, Object>> hotProducts = service.getHotProducts(shopId);
            Map<String, Object> result = new HashMap<>();
            result.put("products", hotProducts);
            return R.ok().addData("data", result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取热门商品数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新订单数据
     */
    @GetMapping("/latest-orders")
    public R getLatestOrders(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        try {
            Long shopId = null;
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                try {
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    return R.error("店铺ID格式错误");
                }
            }
            if (shopId == null || shopId == 0) {
                return R.error("店铺ID不能为空");
            }
            List<Map<String, Object>> latestOrders = service.getLatestOrders(shopId);
            Map<String, Object> result = new HashMap<>();
            result.put("orders", latestOrders);
            return R.ok().addData("data", result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取最新订单数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取店铺趋势数据
     */
    @GetMapping("/trend-data")
    public R getShopTrendData(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        try {
            Long shopId = null;
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                try {
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    return R.error("店铺ID格式错误");
                }
            }
            if (shopId == null || shopId == 0) {
                return R.error("店铺ID不能为空");
            }
            Map<String, Object> trendData = service.getShopTrendData(shopId);
            return R.ok().addData("data", trendData);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取店铺销售统计数据
     */
    @GetMapping("/sales-stats")
    public R getShopSalesStats(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        try {
            Long shopId = null;
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                try {
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    return R.error("店铺ID格式错误");
                }
            }
            if (shopId == null || shopId == 0) {
                return R.error("店铺ID不能为空");
            }
            Map<String, Object> dashboardData = service.getShopDashboardData(shopId);
            return R.ok().addData("data", dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取销售统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取店铺订单统计数据
     */
    @GetMapping("/order-stats")
    public R getShopOrderStats(@RequestParam(value = "shopId", required = false) String shopIdStr) {
        try {
            Long shopId = null;
            if (shopIdStr != null && !shopIdStr.trim().isEmpty()) {
                try {
                    shopId = Long.valueOf(shopIdStr);
                } catch (NumberFormatException e) {
                    return R.error("店铺ID格式错误");
                }
            }
            if (shopId == null || shopId == 0) {
                return R.error("店铺ID不能为空");
            }
            Map<String, Object> dashboardData = service.getShopDashboardData(shopId);
            return R.ok().addData("data", dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取订单统计数据失败: " + e.getMessage());
        }
    }

	/**
	 * 获取店铺营业额排行榜
	 */
	@GetMapping("/rankings/revenue")
	public R getRevenueRankings() {
		try {
			// 查询所有非状态3的店铺
			QueryWrapper<Shop> shopQueryWrapper = new QueryWrapper<>();
			shopQueryWrapper.ne("status", 3);
			List<Shop> shops = service.list(shopQueryWrapper);
			
			List<Map<String, Object>> results = new ArrayList<>();
			
			for (Shop shop : shops) {
				// 查询每个店铺的已完成订单
				QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
				orderQueryWrapper.eq("shop_id", shop.getId());
				orderQueryWrapper.in("status", 0, 1, 2, 4);
				List<Order> orders = orderService.list(orderQueryWrapper);
				
				// 计算总营业额
				double totalRevenue = 0;
				for (Order order : orders) {
					if (order.getTotalAmount() != null) {
						try {
							String amountStr = order.getTotalAmount().toString();
							double amount = Double.parseDouble(amountStr);
							totalRevenue += Math.round(amount * 100.0) / 100.0;
						} catch (NumberFormatException e) {
							System.err.println("金额格式错误: " + order.getTotalAmount());
						}
					}
				}
				
				// 计算客单价
				double avgOrderValue = orders.size() > 0 ? Math.round((totalRevenue / orders.size()) * 100.) / 100 : 0;
				
				// 构建返回数据
				Map<String, Object> result = new HashMap<>();
				result.put("shopId", shop.getId());
				result.put("shopName", shop.getName());
				result.put("logo", shop.getLogo());
				result.put("revenue", totalRevenue);
				result.put("orderCount", orders.size());
				result.put("avgOrderValue", avgOrderValue);
				result.put("status", shop.getStatus());
				result.put("createTime", shop.getCreateTime());
				
				results.add(result);
			}
			
			// 按营业额排序
			results.sort((a, b) -> {
				Double revenueA = (Double) a.get("revenue");
				Double revenueB = (Double) b.get("revenue");
				return revenueB.compareTo(revenueA);
			});
			
			R response = R.ok();
			response.setMessage("查询成功");
			response.addData("shops", results);
			return response;
			
		} catch (Exception e) {
			e.printStackTrace();
			return R.error("查询营业额排行榜失败: " + e.getMessage());
		}
	}

	/**
	 * 获取店铺订单数排行榜
	 */
	@GetMapping("/rankings/orders")
	public R getOrderRankings() {
		try {
			// 查询所有非状态3的店铺
			QueryWrapper<Shop> shopQueryWrapper = new QueryWrapper<>();
			shopQueryWrapper.ne("status", 3);
			List<Shop> shops = service.list(shopQueryWrapper);
			
			List<Map<String, Object>> results = new ArrayList<>();
			
			for (Shop shop : shops) {
				// 查询每个店铺的订单数
				QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
				orderQueryWrapper.eq("shop_id", shop.getId());
				long orderCount = orderService.count(orderQueryWrapper);
				
				// 查询已完成订单计算营业额
				QueryWrapper<Order> completedOrderQueryWrapper = new QueryWrapper<>();
				completedOrderQueryWrapper.eq("shop_id", shop.getId());
				completedOrderQueryWrapper.in("status", 0, 1, 2, 4);
				List<Order> completedOrders = orderService.list(completedOrderQueryWrapper);
				
				double totalRevenue = 0;
				for (Order order : completedOrders) {
					if (order.getTotalAmount() != null) {
						try {
							String amountStr = order.getTotalAmount().toString();
							double amount = Double.parseDouble(amountStr);
							totalRevenue += Math.round(amount * 100.0) / 100.0;
						} catch (NumberFormatException e) {
							System.err.println("金额格式错误: " + order.getTotalAmount());
						}
					}
				}
				
				double avgOrderValue = completedOrders.size() > 0 ? Math.round((totalRevenue / completedOrders.size()) * 100.) / 100 : 0;
				
				// 构建返回数据
				Map<String, Object> result = new HashMap<>();
				result.put("shopId", shop.getId());
				result.put("shopName", shop.getName());
				result.put("logo", shop.getLogo());
				result.put("revenue", totalRevenue);
				result.put("orderCount", orderCount);
				result.put("avgOrderValue", avgOrderValue);
				result.put("status", shop.getStatus());
				result.put("createTime", shop.getCreateTime());
				
				results.add(result);
			}
			
			// 按订单数排序
			results.sort((a, b) -> {
				Long countA = (Long) a.get("orderCount");
				Long countB = (Long) b.get("orderCount");
				return countB.compareTo(countA);
			});
			
			R response = R.ok();
			response.setMessage("查询成功");
			response.addData("shops", results);
			return response;
			
		} catch (Exception e) {
			e.printStackTrace();
			return R.error("查询订单数排行榜失败: " + e.getMessage());
		}
	}

	/**
	 * 获取店铺评分排行榜
	 */
	@GetMapping("/rankings/rating")
	public R getRatingRankings() {
		try {
			// 查询所有非状态3的店铺
			QueryWrapper<Shop> shopQueryWrapper = new QueryWrapper<>();
			shopQueryWrapper.ne("status", 3);
			List<Shop> shops = service.list(shopQueryWrapper);
			
			List<Map<String, Object>> results = new ArrayList<>();
			
			for (Shop shop : shops) {
				// 计算订单数和营业额
				QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
				orderQueryWrapper.eq("shop_id", shop.getId());
				orderQueryWrapper.in("status", 0, 1, 2, 4);
				List<Order> orders = orderService.list(orderQueryWrapper);
				
				double totalRevenue = 0;
				for (Order order : orders) {
					if (order.getTotalAmount() != null) {
						try {
							String amountStr = order.getTotalAmount().toString();
							double amount = Double.parseDouble(amountStr);
							totalRevenue += Math.round(amount * 100.0) / 100.0;
						} catch (NumberFormatException e) {
							System.err.println("金额格式错误: " + order.getTotalAmount());
						}
					}
				}
				
				double avgOrderValue = orders.size() > 0 ? Math.round((totalRevenue / orders.size()) * 100.) / 100 : 0;
				
				// 构建返回数据
				Map<String, Object> result = new HashMap<>();
				result.put("shopId", shop.getId());
				result.put("shopName", shop.getName());
				result.put("logo", shop.getLogo());
				result.put("revenue", totalRevenue);
				result.put("orderCount", orders.size());
				result.put("avgOrderValue", avgOrderValue);
				result.put("status", shop.getStatus());
				result.put("createTime", shop.getCreateTime());
				// 默认评分，实际应该从评价表计算
				result.put("rating", 4.5);
				
				results.add(result);
			}
			
			// 按评分排序
			results.sort((a, b) -> {
				Double ratingA = (Double) a.get("rating");
				Double ratingB = (Double) b.get("rating");
				return ratingB.compareTo(ratingA);
			});
			
			R response = R.ok();
			response.setMessage("查询成功");
			response.addData("shops", results);
			return response;
			
		} catch (Exception e) {
			e.printStackTrace();
			return R.error("查询评分排行榜失败: " + e.getMessage());
		}
	}
}
