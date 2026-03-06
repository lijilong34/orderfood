// API调用文件，用于前后端交互
import axios from 'axios';

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080', // 后端服务地址
  timeout: 10000, // 请求超时时间
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 不添加Authorization头，让未登录用户也能访问
    return config;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 直接返回响应数据
    return response.data;
  },
  error => {
    console.error('响应错误:', error);
    return Promise.reject(error);
  }
);

// 查询商店分类的API方法
export default {
  // 查询商店及其分类
  selectcategoryforshop(params) {
    return service.get('/shop/selectcategoryforshop', {
      params: params
    });
  },
  // 根据商品ID查询商品信息（不需要登录）
  selectproductinfobyproductid(params) {
    return service.get('/selectproductinfobyproductid/' + params);
  },
  // 根据商品ID查询评价（不需要登录）
  getEvaluationsByProductId(productId) {
    return service.get('/evaluation/selectevaluationProductId/' + productId);
  }
};
