import request from "../utils/request";

//地址相关api
export default {
     orderlistbyuser(params={}){
        return request.post('/orderbyUser',params)
    }, orderitembyorderid(params){
        return request.get('/selectorderitembyorderid/'+params);
    }, Cancelorder(orderId){
        return request.get('/Cancelorderbyorder/'+orderId);
    },orderInfo(params={}){
        return request.post('/order/selectorderbyid',params)
    },
    // 历史订单查询（不包含日期限制）
    orderInfoHistory(params={}){
        return request.post('/order/selectorderbyid-nodate',params)
    },
    // 删除订单
    deleteOrder(orderId) {
        return request.get(`/order/delete/${orderId}`)
    }
}
