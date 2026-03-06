// src/api/productApi.js
import request from '../utils/request'; // 只保留一次导入

// 条件+分页查询菜品
export const ProductSelect = (params = {}) => {
  return request.post('/product/selectallproduct', params);
};

// 根据ID查询菜品
export const selectById = (id) => { // 修复函数闭合问题
  return request.get(`/product/getbyid/${id}`);
};

// 根据ID查询菜品（如果需要保留此函数，需确认接口地址是否正确）
export const getProductById = (id) => {
  return request.get(`/product/getbyid/${id}`);
};

// 增加菜品
export const AddProduct = (data) => {
  return request.post('/product/add', data);
};

// 编辑菜品
export const UpdateProduct = (data) => {
  return request.post('/product/updateProduct', data);
};

// 删除菜品
export const DelProduct = (id) => {
  return request.get(`/product/delete/${id}`);
};

// 唯一的默认导出
export default {
  ProductSelect,
  selectById,
  getProductById, // 若保留则加入导出
  AddProduct,
  UpdateProduct,
  DelProduct
};
