import request from "../utils/request";
import axios from 'axios';

// 获取验证码
export const getCode = (phone) => {
  return request({
    url: '/register/sendCode',
    method: 'post',
    data: { phone }
  });
};

// 用户注册
export const userRegister = (form) => {
  return request({
    url: '/register/registeruser',
    method: 'post',
    data: form
  });
};

export const checkPhone=(phone)=>{
   return request({
    url:'/register/checkphone',
    method:'post',
    data:phone
   });
};

export default {
  getCode,
  userRegister,
  checkPhone
};