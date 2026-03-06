import request from '../utils/request'

// 更新订单状态
export const updateOrderStatus = (orderId) => {
  return request.get(`/order/updateorderbyorderid/${orderId}`)
}

export default {
  updateOrderStatus
}