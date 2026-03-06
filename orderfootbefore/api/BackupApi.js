// 数据库备份相关API接口
import request from '../utils/request';
import axios from 'axios';

/**
 * 获取备份列表
 * @param {Object} params - 查询参数
 * @param {number} params.currentPage - 当前页码
 * @param {number} params.pageSize - 每页大小
 * @returns {Promise} 返回备份列表数据
 */
export const getBackupList = (params) => {
  return request({
    url: '/backup/list',
    method: 'get',
    params
  });
};

/**
 * 创建数据备份
 * @returns {Promise} 返回备份结果
 */
export const createBackup = () => {
  return request({
    url: '/backup/create',
    method: 'post'
  });
};

/**
 * 恢复数据备份
 * @param {number} backupId - 备份ID
 * @returns {Promise} 返回恢复结果
 */
export const restoreBackup = (backupId) => {
  return request({
    url: `/backup/restore/${backupId}`,
    method: 'post'
  });
};

/**
 * 删除数据备份
 * @param {number} backupId - 备份ID
 * @returns {Promise} 返回删除结果
 */
export const deleteBackup = (backupId) => {
  return request({
    url: `/backup/delete/${backupId}`,
    method: 'delete'
  });
};

/**
 * 获取备份进度
 * @param {string} backupTaskId - 备份任务ID
 * @returns {Promise} 返回备份进度信息
 */
export const getBackupProgress = (backupTaskId) => {
  return request({
    url: `/backup/progress/${backupTaskId}`,
    method: 'get'
  });
};

/**
 * 导出备份文件
 * @param {number} backupId - 备份ID
 * @returns {Promise} 返回下载链接
 */
export const exportBackup = (backupId) => {
  const token = localStorage.getItem('token');
  
  return axios({
    url: `http://localhost:8080/backup/export/${backupId}`,
    method: 'get',
    headers: {
      'Authorization': token ? `Bearer ${token}` : '',
    },
    responseType: 'blob',
    timeout: 60000
  }).then(response => {
    return {
      data: response.data,
      headers: response.headers
    };
  }).catch(error => {
    console.error('下载备份文件失败:', error);
    throw new Error(`HTTP error! status: ${error.response?.status || 'unknown'}`);
  });
};

/**
 * 导入备份文件
 * @param {FormData} formData - 包含备份文件的FormData
 * @param {Object} options - 导入选项
 * @param {string} options.importPath - 指定导入到的数据库路径
 * @returns {Promise} 返回导入结果
 */
export const importBackup = (formData, options = {}) => {
  // 添加导入路径参数
  if (options.importPath) {
    formData.append('importPath', options.importPath);
  }
  
  return request({
    url: '/backup/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000, // 增加超时时间以处理大型备份文件
  });
};

/**
 * 执行MySQL备份指令并获取备份文件位置
 * @param {Object} params - 备份参数
 * @param {string} params.backupPath - 指定备份文件保存路径
 * @returns {Promise} 返回备份结果和文件位置信息
 */
export const executeMysqlBackup = (params = {}) => {
  return request({
    url: '/backup/execute-mysql-backup',
    method: 'post',
    data: params,
    timeout: 60000, // 增加超时时间以处理大型数据库备份
  });
};

export default {
  getBackupList,
  createBackup,
  restoreBackup,
  deleteBackup,
  getBackupProgress,
  exportBackup,
  importBackup,
  executeMysqlBackup
};
