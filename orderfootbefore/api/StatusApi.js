// StatusApi.js - 系统状态监控API

// 基础URL配置
const baseUrl = 'http://localhost:8080';

// 请求封装
const request = async (url, options = {}) => {
  const token = localStorage.getItem('token');
  
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
    ...options.headers
  };

  try {
    console.log(`发起API请求: ${baseUrl}${url}`);
    const response = await fetch(`${baseUrl}${url}`, {
      ...options,
      headers,
      credentials: 'include' // 处理跨域凭证
    });
    
    console.log(`API请求响应状态: ${response.status}`);
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const data = await response.json();
    console.log(`API请求成功: ${url}`, data);
    return data;
  } catch (error) {
    console.error(`API请求失败: ${url}`, error);
    // 返回一个默认的错误响应对象，避免前端出现undefined错误
    return {
      code: 500,
      message: error.message,
      data: null
    };
  }
};

// 获取系统状态
export const getSystemStatus = async () => {
  return request('/monitor/status', {
    method: 'GET'
  });
};

// 获取CPU使用率
export const getCpuUsage = async () => {
  return request('/monitor/cpu', {
    method: 'GET'
  });
};

// 获取内存使用情况
export const getMemoryUsage = async () => {
  return request('/monitor/memory', {
    method: 'GET'
  });
};

// 获取JVM内存使用情况
export const getJvmMemoryUsage = async () => {
  return request('/monitor/jvm', {
    method: 'GET'
  });
};

// 获取数据库连接池状态
export const getDbPoolStatus = async () => {
  return request('/monitor/db-pool', {
    method: 'GET'
  });
};

// 获取用户数量
export const getUserCount = async () => {
  return request('/user-count/total', {
    method: 'GET'
  });
};
