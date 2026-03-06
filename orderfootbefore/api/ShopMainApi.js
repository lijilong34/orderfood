import request from '../utils/request';

// 获取店铺仪表板数据
export const getShopDashboardData = (params = {}) => {
  const url = `/shop/dashboard?shopId=${params.shopId}`;
  return request.get(url);
};

// 获取店铺销售统计数据
export const getShopSalesStats = (params = {}) => {
  const url = `/shop/sales-stats?shopId=${params.shopId}`;
  return request.get(url);
};

// 获取店铺订单统计数据
export const getShopOrderStats = (params = {}) => {
  const url = `/shop/order-stats?shopId=${params.shopId}`;
  return request.get(url);
};

// 获取热门商品数据
export const getHotProducts = (params = {}) => {
  const url = `/shop/hot-products?shopId=${params.shopId}`;
  return request.get(url);
};

// 获取最新订单数据
export const getLatestOrders = (params = {}) => {
  const url = `/shop/latest-orders?shopId=${params.shopId}`;
  return request.get(url);
};

// 获取店铺趋势数据
export const getShopTrendData = (params = {}) => {
  const url = `/shop/trend-data?shopId=${params.shopId}`;
  return request.get(url);
};
