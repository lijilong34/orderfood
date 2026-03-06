// LogApi.js - 日志管理API

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
    // 返回一个默认的错误响应对象
    return {
      code: 500,
      message: error.message,
      data: null
    };
  }
};

// 获取日志列表
export const getLogList = async (params = {}) => {
  // 构建查询参数
  const queryParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      queryParams.append(key, value);
    }
  });
  
  return request(`/log/list?${queryParams.toString()}`, {
    method: 'GET'
  });
};

// 根据ID获取日志详情
export const getLogById = async (logId) => {
  return request(`/log/getById/${logId}`, {
    method: 'GET'
  });
};

// 删除日志
export const deleteLog = async (logId) => {
  return request(`/log/delete/${logId}`, {
    method: 'DELETE'
  });
};

// 批量删除日志
export const batchDeleteLog = async (logIds) => {
  return request('/log/batchDelete', {
    method: 'DELETE',
    body: JSON.stringify(logIds)
  });
};

// 按时间范围删除日志
export const deleteLogByTimeRange = async (startTime, endTime) => {
  return request(`/log/deleteByTimeRange?startTime=${startTime}&endTime=${endTime}`, {
    method: 'DELETE'
  });
};
