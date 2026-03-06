import request from '../utils/request'


export const orderInfo = (params={}) => {
  return request.post('/order/selectorderbyid',params)
}
// /order/edit 订单修改
export const orderEdit = (data) => {
  return request.post('/order/edit', data)
}
//根据shopid查询
export const orderlist = () => {
  return request.get('/order/selectbyorderid?shopid='+localStorage.getItem('shopid'))
}

// 创建订单
export const createOrder = (data) => {
  return request.post('/order/create', data)
}